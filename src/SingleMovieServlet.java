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
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;


// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
//            String query = "SELECT * from stars s, stars_in_movies sim, movies m " +
//                    "WHERE m.id = sim.movieId and sim.starId = s.id and s.id = ?";
            String getMovieQuery = "SELECT * FROM movies WHERE id = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(getMovieQuery);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet movieInfo = statement.executeQuery();
            movieInfo.next();
            JsonObject jsonObject = new JsonObject();
            String title = movieInfo.getString("title");
            String director = movieInfo.getString("director");
            jsonObject.addProperty("title",title);
            jsonObject.addProperty("director",director);

            movieInfo.close();
            statement.close();

            String getRatingQuery = "SELECT rating FROM ratings WHERE movieId = \"" + id + "\"";
            Statement getRatingStatement = conn.createStatement();
            ResultSet ratingResult = getRatingStatement.executeQuery(getRatingQuery);
            ratingResult.next();

            Float rating = ratingResult.getFloat("rating");
            jsonObject.addProperty("rating",rating);

            getRatingStatement.close();
            ratingResult.close();


            JsonArray jsonArray = new JsonArray();
            jsonArray.add(jsonObject);
            /*
            [
                {"title": xxxx, "director": xxxx, "rating": xxxx},

            ]
             */

//            String getStarsQuery = "SELECT * FROM stars_in_movies sim, stars s WHERE s.id = sim.starId AND sim.movieId = \"" + id + "\"";
//            Statement getStarsStatement = conn.createStatement();
//            ResultSet starsResult = getStarsStatement.executeQuery(getStarsQuery);
//
//            JsonArray starsJsonArr = new JsonArray();
//
//            /*
//
//            jsonArray:
//
//            [
//                jsonObject: {"title": xxxx, "director": xxxx, "rating": xxxx}
//
//                starsJsonArray: [
//                            { "star1_name": xxxx, "star1_id": xxx},
//                            { "star2_name": xxxx, "star2_id": xxx}]
//
//                 genresJsonArray: [
//
//
//             */
//
//            getStarsStatement.close();
//            starsResult.close();
//
//            String getGenresQuery = "SELECT name FROM genres_in_movies gim, genres g WHERE g.id = gim.genreId AND gim.movieId = \"" + id + "\"";
//            Statement getGenresStatement = conn.createStatement();
//            ResultSet genresResult = getGenresStatement.executeQuery(getGenresQuery);
//
//            JsonObject genresJson = new JsonObject();
//            while(genresResult.next()){
//                //genresJson.addProperty;
//            }

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            System.out.println(jsonObject);
            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
