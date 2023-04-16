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
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

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
            String getStarQuery = "SELECT * FROM stars WHERE id = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(getStarQuery);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet starInfo = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (starInfo.next()) {

                String starId = starInfo.getString("id");
                String starName = starInfo.getString("name");
                String starDob = starInfo.getString("birthYear");
//                String movieId = starInfo.getString("movieId");
//                String movieTitle = starInfo.getString("title");
//                String movieYear = starInfo.getString("year");
//                String movieDirector = starInfo.getString("director");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", starId);
                jsonObject.addProperty("star_name", starName);
                if(starDob == null){
                    jsonObject.addProperty("star_dob","N/A");
                }
                else{
                    jsonObject.addProperty("star_dob", starDob);
                }
//                jsonObject.addProperty("movie_id", movieId);
//                jsonObject.addProperty("movie_title", movieTitle);
//                jsonObject.addProperty("movie_year", movieYear);
//                jsonObject.addProperty("movie_director", movieDirector);

                jsonArray.add(jsonObject);
            }

            String getMoviesQuery = "SELECT * FROM movies m, stars_in_movies sim WHERE sim.movieId = m.id AND sim.starId = \"" + id + "\"";
            Statement getMoviesStatement = conn.createStatement();


            ResultSet movies = getMoviesStatement.executeQuery(getMoviesQuery);
            while(movies.next()){
                String movieId = movies.getString("movieId");
                String movieTitle = movies.getString("title");
                String movieYear = movies.getString("year");
                String movieDirector = movies.getString("director");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("title", movieTitle);
                jsonObject.addProperty("year", movieYear);
                jsonObject.addProperty("director", movieDirector);

                jsonArray.add(jsonObject);
            }
            getMoviesStatement.close();
            movies.close();
            starInfo.close();
            statement.close();

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
