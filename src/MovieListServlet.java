import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Declare our statement
            Statement statement = conn.createStatement();
            String query;
            int page = 1;
            int resultsPerPage = 25;
            int offset = page * resultsPerPage;
            String limitOffset = "LIMIT " + resultsPerPage + " OFFSET " + offset;
//            String ratingQuery = " AND r.movieId = m.id";
            if(!searchByGenre.equals("")){

                query = "SELECT m.id, m.title, m.year, m.director FROM movies m, genres_in_movies gim, genres g, ratings r "+
                        "WHERE m.id = gim.movieId AND gim.genreId = g.id AND g.name = \"" + searchByGenre + "\"" +
                        limitOffset;

            }
            else if(!searchByTitle.equals("")){
                searchByTitle += "%";
//                query = "SELECT r.movieId, r.rating FROM ratings r, movies m WHERE r.movieId = m.id AND m.title LIKE \"" +
//                        searchByTitle + "\"";
                query = "SELECT m.id, m.title, m.year, m.director FROM movies m WHERE m.title LIKE \"" + searchByTitle + "\"" + limitOffset;

            }
            else{
                query = "SELECT m.id, r.rating FROM ratings r, movies m WHERE r.movieId = m.id ORDER BY r.rating DESC LIMIT 20 ";
            }

//            String query = "SELECT movieId, rating FROM ratings ORDER BY rating DESC LIMIT 20 ";

            // Perform the query
            ResultSet movieIDSet = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while(movieIDSet.next()){
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
                String movieID = movieIDSet.getString("id");
                String title = movieIDSet.getString("title");
                int year = movieIDSet.getInt("year");
                String director = movieIDSet.getString("director");

                String getGenresQuery = "SELECT DISTINCT g.name FROM genres g, genres_in_movies gim WHERE gim.movieId = \"" + movieID + "\" AND gim.genreId = g.id";
                Statement getGenresStatement = conn.createStatement();
                ResultSet genresSet = getGenresStatement.executeQuery(getGenresQuery);
                ArrayList<String> genresList = new ArrayList<>();
                while(genresSet.next()){
                    genresList.add(genresSet.getString("name"));
                }
                Collections.sort(genresList);

//                String getStarsQuery = "SELECT"
            }
            // Iterate through each row of rs
//            while (rs.next()) {
//                String movieID = rs.getString("movieId");
////                System.out.println(movieID);
//                float rating = rs.getFloat("rating");
//                // deal with genres and stars later
//                String getMovieQuery = "SELECT * FROM movies WHERE id = \"" + movieID + "\"";
//                Statement getMovieStatement = conn.createStatement();
//                ResultSet movieData = getMovieStatement.executeQuery(getMovieQuery);
//                movieData.next();
//
//                String title = movieData.getString("title");
//                Integer year = movieData.getInt("year");
//                String director = movieData.getString("director");
//
//                String getGenresQuery = "SELECT g.name FROM genres_in_movies gim, genres g WHERE g.id = gim.genreId AND gim.movieId = \"" + movieID + "\" LIMIT 3";
//                Statement getGenresStatement = conn.createStatement();
//                ResultSet genresData = getGenresStatement.executeQuery(getGenresQuery);
//                String genres = "";
//                while(genresData.next()){
//                 genres += genresData.getString("name");
//                 genres += ", ";
//                }
//                genres = genres.substring(0,genres.length()-2);
//                // Create a JsonObject based on the data we retrieve from rs
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("movieID", movieID);
//                jsonObject.addProperty("title", title);
//                jsonObject.addProperty("year", year);
//                jsonObject.addProperty("director", director);
//                jsonObject.addProperty("rating", rating);
//                jsonObject.addProperty("genres", genres);
//
//                movieData.close();
//                getMovieStatement.close();
//
//                String getStarsQuery = "SELECT s.name, s.id FROM stars s, stars_in_movies sim WHERE s.id = sim.starId AND sim.movieId = \"" + movieID + "\" LIMIT 3";
//                Statement getStarsStatement = conn.createStatement();
//                ResultSet starsResult = getStarsStatement.executeQuery(getStarsQuery);
//                starsResult.next();
//                jsonObject.addProperty("star1_name", starsResult.getString("name"));
//                jsonObject.addProperty("star1_id", starsResult.getString("id"));
//                starsResult.next();
//                jsonObject.addProperty("star2_name", starsResult.getString("name"));
//                jsonObject.addProperty("star2_id", starsResult.getString("id"));
//                starsResult.next();
//                jsonObject.addProperty("star3_name", starsResult.getString("name"));
//                jsonObject.addProperty("star3_id", starsResult.getString("id"));
//
//                jsonArray.add(jsonObject);
//                getStarsStatement.close();
//                starsResult.close();
//
//            }
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
