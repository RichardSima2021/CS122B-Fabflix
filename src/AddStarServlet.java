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
@WebServlet(name = "AddStarServlet", urlPatterns = "/api/add_star")
public class AddStarServlet extends HttpServlet{
    private DataSource dataSource;

    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");        }
        catch(NamingException e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String name = request.getParameter("name");
        int year;
        String birthYear = request.getParameter("birth_year");
        if(birthYear.equals("")){
            year = -1;
        }
        else{
            year = Integer.parseInt(birthYear);
        }

        PrintWriter out = response.getWriter();

//        System.out.println("Add " + name + " Birthyear: " + year + "|");

        try(Connection conn = dataSource.getConnection()){
            CallableStatement getMaxStarID = conn.prepareCall("CALL getMaxStarID()");
            ResultSet rs = getMaxStarID.executeQuery();
            rs.next();
//            System.out.println(rs.getString("MAX(id)"));
            int id = Integer.parseInt(rs.getString("MAX(id)").substring(2)) +1;
            String newID = "nm" + id;
            String newStarQuery = "INSERT INTO stars(id, name, birthYear) VALUES(?,?,?)";
            PreparedStatement newStarStatement = conn.prepareStatement(newStarQuery);
            newStarStatement.setString(1,newID);
            newStarStatement.setString(2,name);
            if(year < 0){
                newStarStatement.setString(3,null);
            }
            else{
                newStarStatement.setInt(3,year);
            }
            newStarStatement.executeUpdate();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "success");
            jsonObject.addProperty("newID", newID);

            rs.close();
            newStarStatement.close();
            out.write(jsonObject.toString());
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
