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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.IOException;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private DataSource dataSource;
    private static final long serialVersionUID = 1L;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        int id;
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {

            return;
        }

        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT password, id FROM customers WHERE email = ?";
            PreparedStatement getUserStatement = conn.prepareStatement(query);
            getUserStatement.setString(1,email);
            ResultSet rs = getUserStatement.executeQuery();
            if(rs.next() == false){
                // no such user
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "email " + email + " doesn't exist");
            }
            else{
                String encryptedPassword = rs.getString("password");
                boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                if(success){
                    id = rs.getInt("id");
                    request.getSession().setAttribute("user", new User(email, id));
                    request.getSession().setAttribute("accountType","user");
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else{
                    // wrong password
                    responseJsonObject.addProperty("status", "fail");
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            response.getWriter().write(responseJsonObject.toString());
        }catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
            response.getWriter().write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            response.getWriter().close();
        }

    }
}
