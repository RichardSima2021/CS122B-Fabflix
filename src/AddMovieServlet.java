import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jshell.spi.ExecutionControlProvider;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.io.IOException;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/add_movie")
public class AddMovieServlet extends HttpServlet{
    private DataSource dataSource;

    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");        }
        catch(NamingException e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String movieTitle = request.getParameter("title");
        int movieYear = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String starName = request.getParameter("star_name");
        String birthYearStr = request.getParameter("birth_year");
        String genre = request.getParameter("genre");

        System.out.println("Title: " + movieTitle);
        System.out.println("Movie Year: " + movieYear);
        System.out.println("Director: " + director);
        System.out.println("Star: " + starName);
        System.out.println("Birth Year: " + birthYearStr);
        System.out.println("Genre: " + genre);



        PrintWriter out = response.getWriter();

        try(Connection conn = dataSource.getConnection()){
            CallableStatement addMovieStatement = conn.prepareCall("CALL add_movie(?,?,?,?,?,?,?,?,?,?,?,?)");
            addMovieStatement.setString(1,movieTitle);
            addMovieStatement.setInt(2, movieYear);
            addMovieStatement.setString(3, director);
            addMovieStatement.setString(4, starName);



            if(birthYearStr.equals("")){
                addMovieStatement.setString(5, null);
            }
            else{
                addMovieStatement.setInt(5, Integer.parseInt(birthYearStr));
            }
            addMovieStatement.setString(6,genre);


            addMovieStatement.registerOutParameter(7, java.sql.Types.VARCHAR);
            addMovieStatement.registerOutParameter(8, java.sql.Types.VARCHAR);
            addMovieStatement.registerOutParameter(9, java.sql.Types.VARCHAR);
            addMovieStatement.registerOutParameter(10, Types.INTEGER);
            addMovieStatement.registerOutParameter(11, Types.BOOLEAN);
            addMovieStatement.registerOutParameter(12, Types.BOOLEAN);

            System.out.println(addMovieStatement.toString());

//            System.out.println("Debug");

            addMovieStatement.executeUpdate();
            JsonObject resultJson = new JsonObject();
            String insertStatus = addMovieStatement.getString(7);
            if(insertStatus.equals("Inserted")){
                String movieID = addMovieStatement.getString(8);
                String starID = addMovieStatement.getString(9);
                int genreID = addMovieStatement.getInt(10);
                resultJson.addProperty("status","Inserted");
                resultJson.addProperty("movieID",movieID);

                if(addMovieStatement.getBoolean(11)){
                    resultJson.addProperty("newStarEntry", true);
                    resultJson.addProperty("starID", starID);
                }
                else{
                    resultJson.addProperty("newStarEntry",false);
                }

                if(addMovieStatement.getBoolean(12)){
                    resultJson.addProperty("newGenreEntry",true);
                    resultJson.addProperty("genreID", genreID);
                }
                else{
                    resultJson.addProperty("newGenreEntry",false);
                }

            }
            else{
                resultJson.addProperty("status", "Exists");
            }

            addMovieStatement.close();
            out.write(resultJson.toString());
            response.setStatus(200);
        }
        catch(Exception e){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "failed");
            jsonObject.addProperty("errorMessage",e.getMessage());
            System.out.println(jsonObject);
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        finally{
            out.close();
        }


    }
}