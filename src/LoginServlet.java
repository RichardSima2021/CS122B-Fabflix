import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");



        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            Statement getUserStatement = conn.createStatement();

            String query = "SELECT password FROM customers WHERE email = \"" + username + "\"";
            ResultSet rs = getUserStatement.executeQuery(query);
            if(rs.next() == false){
                // no such user
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            }
            else{
                String checkPassword = rs.getString("password");
                if(!checkPassword.equals(password)){
                    // wrong password
                    responseJsonObject.addProperty("status", "fail");
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
                else{
                    // existing user and correct password
                    request.getSession().setAttribute("user", new User(username));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
            }

        }catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
            response.getWriter().write(responseJsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }


//        if (username.equals("anteater") && password.equals("123456")) {
//            // Login success:
//
//            // set this user into the session
//            request.getSession().setAttribute("user", new User(username));
//
//            responseJsonObject.addProperty("status", "success");
//            responseJsonObject.addProperty("message", "success");
//
//        } else {
//            // Login fail
//            responseJsonObject.addProperty("status", "fail");
//            // Log to localhost log
//            request.getServletContext().log("Login failed");
//            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
//            if (!username.equals("anteater")) {
//                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
//            } else {
//                responseJsonObject.addProperty("message", "incorrect password");
//            }
//        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
