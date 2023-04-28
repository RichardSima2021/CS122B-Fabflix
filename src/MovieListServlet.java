import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movie-list"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
//        System.out.println("debug");
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); // Response mime type

        String searchByGenre = request.getParameter("searchByGenre");
        String searchByTitle = request.getParameter("searchByTitle");
        String sortOrder = request.getParameter("sortOrder");
        String perPage = request.getParameter("perPage");

        HttpSession session = request.getSession();

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Declare our statement
            Statement statement = conn.createStatement();
            String query;
            int page = 1;
            int resultsPerPage;
            String orderBy = " ORDER BY ";
            if(sortOrder.equals("")){
                orderBy += (String) session.getAttribute("sortOrder");
            }
            else{
                orderBy += sortOrder;
                session.setAttribute("sortOrder", orderBy);
            }
            if(perPage.equals("")){
                resultsPerPage = (int) session.getAttribute("resultsPerPage");
            }
            else{
                resultsPerPage = Integer.parseInt(perPage);
                session.setAttribute("resultsPerPage", resultsPerPage);
            }
            int offset = (page-1) * resultsPerPage;
            String limitOffset = "LIMIT " + resultsPerPage + " OFFSET " + offset;
//            String ratingQuery = " AND r.movieId = m.id";
            if(!searchByGenre.equals("")){

                query = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies m, genres_in_movies gim, genres g, ratings r "+
                        "WHERE m.id = gim.movieId AND gim.genreId = g.id AND g.name = \"" + searchByGenre + "\"" + "AND r.movieId = m.id" +
                        orderBy +
                        limitOffset;
                session.setAttribute("query", query);

            }
            else if(!searchByTitle.equals("")){
                searchByTitle += "%";
                query = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies m, ratings r WHERE m.title LIKE \"" + searchByTitle + "\"" + "AND r.movieId = m.id" + orderBy + limitOffset;
                session.setAttribute("query", query);
            }
            else{
                query = (String) session.getAttribute("query");
            }

            System.out.println(query);

            // Perform the query
            ResultSet movieIDSet = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while(movieIDSet.next()){
                // For each movie in the result set:
                /*

                Test movieIDs:
                tt0218126
                tt0239235

                Info to get:
                movies table:
                    - title
                    - year
                    - director

                genres, genres_in_movies:
                First Three Genres

                stars, stars_in_movies:
                First Three Stars (by number of movies played by each star?)

                ratings:
                rating

                 */
                JsonObject movieInfo = new JsonObject();

                String movieID = movieIDSet.getString("id");
                String title = movieIDSet.getString("title");
                int year = movieIDSet.getInt("year");
                String director = movieIDSet.getString("director");

                movieInfo.addProperty("movieID", movieID);
                movieInfo.addProperty("title", title);
                movieInfo.addProperty("year", year);
                movieInfo.addProperty("director", director);


                String getGenresQuery = "SELECT DISTINCT g.name FROM genres g, genres_in_movies gim WHERE gim.movieId = \"" + movieID + "\" AND gim.genreId = g.id";
                Statement getGenresStatement = conn.createStatement();
                ResultSet genresSet = getGenresStatement.executeQuery(getGenresQuery);
                ArrayList<String> genresList = new ArrayList<>();
                while(genresSet.next()){
                    genresList.add(genresSet.getString("name"));
                }
                Collections.sort(genresList);
                JsonArray genreJsonArr = new JsonArray();

                for(int i = 0; (i < genresList.size() && i < 3); i++){
                    genreJsonArr.add( genresList.get(i));
                }
                movieInfo.add("genres", genreJsonArr);
                genresSet.close();



                String getStarsQuery = "WITH stars_in_this_movie AS("+
                        "SELECT s1.id, s1.name FROM stars s1, stars_in_movies sim1 "+
                        "WHERE sim1.movieId = \"" + movieID + "\" AND sim1.starId = s1.Id) " +
                        "SELECT s.name, s.id, COUNT(m.id) as movieCount "+
                        "FROM stars_in_this_movie s, movies m, stars_in_movies sim " +
                        "WHERE sim.starId = s.id AND sim.movieId = m.id " +
                        "GROUP BY s.name ORDER BY movieCount DESC, name ASC LIMIT 3";
                Statement getStarsStatement = conn.createStatement();
                ResultSet topStars = getStarsStatement.executeQuery(getStarsQuery);
                ArrayList<String> starsList = new ArrayList<>();
                ArrayList<String> starsIDList = new ArrayList<>();

                while(topStars.next()){
                    starsList.add(topStars.getString("name"));
                    starsIDList.add(topStars.getString("id"));
                }

                JsonArray starsJsonArr = new JsonArray();
                JsonArray starsIDJsonArr = new JsonArray();
                for(int i = 0; (i<starsList.size() && i < 3); i++){
                    starsJsonArr.add(starsList.get(i));
                    starsIDJsonArr.add(starsIDList.get(i));
                }
                movieInfo.add("stars_name", starsJsonArr);
                movieInfo.add("stars_id", starsIDJsonArr);
                topStars.close();



                String getRatingQuery = "SELECT rating FROM ratings WHERE movieId = \"" + movieID + "\"";
                Statement getRatingStatement = conn.createStatement();
                ResultSet ratingRes = getRatingStatement.executeQuery(getRatingQuery);
                if(ratingRes.next() == false){
                    movieInfo.addProperty("rating","N/A");
                }
                else{
//                    ratingRes.next();
                    float rating = ratingRes.getFloat("rating");
                    movieInfo.addProperty("rating",rating);
                }


                ratingRes.close();


                jsonArray.add(movieInfo);
            }

            movieIDSet.close();
            statement.close();


            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
