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
        System.out.println(request.getQueryString());
        // Don't use session to directly store query, use session to reconstruct query

        /*
            This servlet is called under four scenarios:
            1. Browse
            2. Search
            3. Update
            4. Return from single page

            1.
                Browse:
                    - Either browse by genre or by title: generate two queries
                    - Check request parameter for sort order and max per page

            2.
                Search:
                    - Search has four possible conditions, build single query off that
                    - check request parameter for sort order and max per page

            3.
                Update:
                    - Update will keep last used query parameters except change the ORDER BY and LIMIT sections
                    - Get those parameters from session..?

            4.
                Return from single page:
                    - Was prior query a browse or search?



         */
        String filter = request.getParameter("filter"); // This lets us know which of the four scenarios to deal with

        String sortOrder = request.getParameter("sortOrder"); // These will always be present
        String perPage = request.getParameter("perPage");


        String browseByGenre = request.getParameter("browseByGenre");
        String browseByTitle = request.getParameter("browseByTitle");


        String searchByTitle = request.getParameter("searchByTitle");
        String searchByYear = request.getParameter("searchByYear");
        String searchByDirector = request.getParameter("searchByDirector");
        String searchByStar = request.getParameter("searchByStar");

        String pageNumStr = request.getParameter("pageNum");

        System.out.println("Parameters requested");
        int page;


        HttpSession session = request.getSession();
        System.out.println("Session retrieved");

        if(pageNumStr == null){
            System.out.println("JS didn't send a page number");
            // came back from another page, no pageNum info
            if(session.getAttribute("currentPageStr") == null){
                page = 1;
                session.setAttribute("currentPageStr", 1);
                System.out.println("There was no stored page number in session, it's now 1");
            }
            else{
                page = (int) session.getAttribute("currentPageStr");
                System.out.println("Retrieved page number " + page + " from session");
            }
        }
        else{
            System.out.println("JS sent a page number");
            page = Integer.parseInt(pageNumStr);
            session.setAttribute("currentPageStr",page);
            System.out.println("page number is " + page);
        }

        System.out.println("Currently on page " + page);

        // If session does not currently store results per page - back from single page
        // otherwise results per page are given by the other three scenarios
        if(perPage.equals("")){
//            System.out.println("Grabbing perPage from session: ");
            perPage = (String) session.getAttribute("resultsPerPage");
            if(perPage == null){
                perPage = "10";
            }
//            System.out.println(perPage + "per Page");
        }
        else{
            session.setAttribute("resultsPerPage", perPage);
        }
        System.out.println(perPage + " per page");
        if(sortOrder.equals("")){
//            System.out.println("Grabbing sortOrder from session: ");
            sortOrder = (String) session.getAttribute("sortOrder");
            if(sortOrder == null){
                sortOrder = "TITLE ASC, RATING ASC ";
                session.setAttribute("sortOrder",sortOrder);
            }
//            System.out.println(sortOrder);
        }
        else{
            session.setAttribute("sortOrder", sortOrder);
        }
        System.out.println("Sort by " + sortOrder);

//        System.out.println("Tf");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Declare our statement
            Statement statement = conn.createStatement();
            String query = "WITH filtered AS( ";
            String selectFiltered = "SELECT f.id, f.title, f.year, f.director, IFNULL(r.rating,0) FROM filtered f LEFT JOIN ratings r ON f.id = r.movieId";
//            int page = 1;
            int resultsPerPage = Integer.parseInt(perPage);
            int offset = (page-1) * resultsPerPage;

            String orderBy = " ORDER BY "; // This may be changed by update or search

            String limitOffset = "LIMIT " + resultsPerPage + " OFFSET " + offset; // this is always auto completed
//            System.out.println(limitOffset);
            if(filter.equals("browse")){
//                System.out.println("Filter: browse");
                if(!browseByGenre.equals("")){
                    // Browse By Genre
                    query += "SELECT m.id, m.title, m.year, m.director FROM movies m, genres_in_movies gim, genres g "+
                            "WHERE m.id = gim.movieId AND gim.genreId = g.id AND g.name = \"" + browseByGenre + "\")";

                }
                else if(!browseByTitle.equals("")){
                    // Browse By Title
                    if(browseByTitle.equals("*")){
                        query += "SELECT m.id, m.title, m.year, m.director FROM movies m WHERE m.title REGEXP '^[^a-zA-Z0-9]')";
                    }
                    else{
                        browseByTitle += "%";
                        query += "SELECT m.id, m.title, m.year, m.director FROM movies m WHERE m.title LIKE \"" + browseByTitle + "\")";
                    }

                }
                else{
//                    System.out.println("Return from single page");
                    // return would still submit a browse but without any parameters
                    query = (String) session.getAttribute("query");
                    sortOrder = (String) session.getAttribute("sortOrder");
                }
                orderBy += sortOrder;
                session.setAttribute("query", query);
            }
            else if(filter.equals("search")){
//                System.out.println("Search");
                String withStatement = "WITH filtered AS(";
                String select = "SELECT m.id, m.title, m.year, m.director ";
                String from = "FROM movies m ";
                String where = "WHERE m.id = m.id ";
                if(!searchByStar.equals("")){
                    from += " ,stars_in_movies sim, stars s ";
                    where += "AND sim.movieId = m.id AND sim.starId = s.id AND s.name LIKE \"%" + searchByStar + "%\"" ;
                }
                if(!searchByTitle.equals("")){
                    where += "AND m.title LIKE \"%" + searchByTitle + "%\" ";
                }
                if(!searchByYear.equals("")){
                    where += "AND m.year = " + searchByYear + " ";
                }
                if(!searchByDirector.equals("")){
                    where += "AND m.director LIKE \"%" + searchByDirector + "%\"";
                }
                where += ")";
                query = withStatement + select + from + where;

                session.setAttribute("sortOrder", sortOrder);
                orderBy += sortOrder;

                session.setAttribute("query", query);
            }
            else /*if(filter.equals("update"))*/{
//                System.out.println("Filter: update");
                query = (String) session.getAttribute("query");

                session.setAttribute("sortOrder", sortOrder);
                orderBy += sortOrder;
            }

            query = query + selectFiltered + orderBy + limitOffset;

//            System.out.println("debug");
            System.out.println(query);
//            System.out.println("debug");

            // Perform the query
            ResultSet movieIDSet = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();
            if(movieIDSet.next() == false){
                throw new RuntimeException("no results on this page");
            }
            else{
                do{
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
                            "GROUP BY s.name, s.id ORDER BY movieCount DESC, name ASC LIMIT 3";
//                System.out.println(getStarsQuery);
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
                }while(movieIDSet.next());
            }

              //sends page number back out
            jsonArray.add(page);

            movieIDSet.close();
            statement.close();


            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");
//            System.out.println(jsonArray.size());
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
