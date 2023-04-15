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
            int year = movieInfo.getInt("year");
            String director = movieInfo.getString("director");
            jsonObject.addProperty("title",title);
            jsonObject.addProperty("year", year);
            jsonObject.addProperty("director",director);

            movieInfo.close();
            statement.close();

            String getRatingQuery = "SELECT rating FROM ratings WHERE movieId = \"" + id + "\"";
            Statement getRatingStatement = conn.createStatement();
            ResultSet ratingResult = getRatingStatement.executeQuery(getRatingQuery);
            ratingResult.next();
            Float rating = null;
            try{
                rating = ratingResult.getFloat("rating");
            }
            catch (Exception e)
            {
            }

            jsonObject.addProperty("rating",rating);


            getRatingStatement.close();
            ratingResult.close();

            String getGenresQuery = "SELECT * FROM genres_in_movies gim, genres g WHERE g.id = gim.genreId AND gim.movieId = \"" + id + "\"";
            Statement getGenresStatement = conn.createStatement();
            ResultSet genresResult = getGenresStatement.executeQuery(getGenresQuery);
            String genres = "";

            while(genresResult.next()){
                genres += genresResult.getString("name");
                genres += ", ";
            }
            genres = genres.substring(0,genres.length()-2);


            jsonObject.addProperty("genres", genres);


            JsonArray jsonArray = new JsonArray();
            jsonArray.add(jsonObject);
            /*
            [
                {"title": xxxx, "director": xxxx, "rating": xxxx}, | resultData[0]
                {"stars_names": [star1_name, star2_name...]},        | resultData[1]
                {"star_IDs": [star1_id, star2_id ... ]},
            ]
             */

            JsonObject starsNames = new JsonObject();
            JsonObject starsIDs = new JsonObject();
            JsonArray starNamesArray = new JsonArray();
            JsonArray starIDsArray = new JsonArray();

            String getStarsQuery = "SELECT * FROM stars s, stars_in_movies sim WHERE sim.starId = s.id AND sim.movieId = \"" + id + "\"";
            Statement getStarsStatement = conn.createStatement();
            ResultSet starsResult = getStarsStatement.executeQuery(getStarsQuery);
            System.out.println("Stars query executed");
            while(starsResult.next()){
                String currentStarName = starsResult.getString("name");
                String currentStarID = starsResult.getString("id");
                starNamesArray.add(currentStarName);
                starIDsArray.add(currentStarID);
            }

            getStarsStatement.close();
            starsResult.close();

            starsNames.add("stars_names", starNamesArray);
            jsonArray.add(starsNames);

            starsIDs.add("stars_ids", starIDsArray);
            jsonArray.add(starsIDs);




            getGenresStatement.close();
            genresResult.close();


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
