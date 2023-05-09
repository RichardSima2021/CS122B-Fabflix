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


// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
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

            JsonArray jsonArray = new JsonArray();


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
            String movieId = movieInfo.getString("id");
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
                jsonObject.addProperty("rating",rating);
            }
            catch (Exception e)
            {
                jsonObject.addProperty("rating","N/A");
            }




            getRatingStatement.close();
            ratingResult.close();

            JsonObject genresNames = new JsonObject();
            JsonObject genresIDs = new JsonObject();
            JsonArray genreNamesArray = new JsonArray();
            JsonArray genreIDsArray = new JsonArray();

            String getGenresQuery = "SELECT g.id, g.name FROM genres_in_movies gim, genres g, movies m WHERE " +
                    "gim.genreId = g.id AND m.id = \"" + movieId + "\" AND gim.movieId = m.id ORDER BY g.name ASC";
            Statement getGenresStatement = conn.createStatement();
            ResultSet genresResult = getGenresStatement.executeQuery(getGenresQuery);

            while(genresResult.next()){
                String currentGenreName = genresResult.getString("name");
                String currentGenreId = genresResult.getString("id");
                genreNamesArray.add(currentGenreName);
                genreIDsArray.add(currentGenreId);
            }
            getGenresStatement.close();
            genresResult.close();



//            jsonObject.addProperty("genres", genres);


            jsonArray.add(jsonObject);

            genresNames.add("genres_names",genreNamesArray);
            jsonArray.add(genresNames);
            genresIDs.add("genres_ids",genreIDsArray);
            jsonArray.add(genresIDs);
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

//            String getStarsQuery = "SELECT * FROM stars s, stars_in_movies sim WHERE sim.starId = s.id AND sim.movieId = \"" + id + "\"";
            String getStarsQuery = "WITH stars_in_this_movie AS (" +
                    "SELECT s1.id, s1.name FROM stars s1, stars_in_movies sim1 " +
                    "WHERE sim1.movieId = \"" + movieId + "\" AND sim1.starId = s1.Id) " +
                    "SELECT s.name, s.id, COUNT(m.id) as movieCount " +
                    "FROM stars_in_this_movie s, movies m, stars_in_movies sim "+
                    "WHERE sim.starId = s.id AND sim.movieId = m.id " +
                    "GROUP BY s.name, s.id ORDER BY movieCount DESC, name ASC";
            Statement getStarsStatement = conn.createStatement();
            ResultSet starsResult = getStarsStatement.executeQuery(getStarsQuery);
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

//            System.out.println(jsonArray.toString());
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
