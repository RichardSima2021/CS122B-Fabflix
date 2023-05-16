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

@WebServlet(name = "DashboardLoginServlet", urlPatterns = "/api/dashboard_login")
public class DashboardLoginServlet extends HttpServlet{
    private DataSource dataSource;
//    Idk what this does
//    private static final long serialVersionUID = 3L;

    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }
        catch (NamingException e){
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String email = request.getParameter("email");
        String providedPassword = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);

            System.out.println("Attempted login with: " + email + ", " + providedPassword);


            try(Connection conn = dataSource.getConnection()){
                String query = "SELECT password, email, fullname FROM employees WHERE email = ?";
                PreparedStatement getEmployeeStatement = conn.prepareStatement(query);
                getEmployeeStatement.setString(1,email);
                ResultSet rs = getEmployeeStatement.executeQuery();

                if(rs.next() == false){
                    // no such user
                    responseJsonObject.addProperty("status", "fail");
                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "Employee email " + email + " doesn't exist");
                }
                else{
                    String encryptedPassword = rs.getString("password");
                    boolean success = new StrongPasswordEncryptor().checkPassword(providedPassword,encryptedPassword);
                    if(success){
                        String fullname = rs.getString("fullname");
                        request.getSession().setAttribute("employee", new Employee(email, fullname));
                        request.getSession().setAttribute("accountType","employee");
                        responseJsonObject.addProperty("status","success");
                        responseJsonObject.addProperty("message","success");
                    }
                    else{
                        // wrong password
                        responseJsonObject.addProperty("status","fail");
                        responseJsonObject.addProperty("message","incorrect password");
                    }
                }
                out.write(responseJsonObject.toString());
                response.setStatus(200);
            }
            catch(Exception e){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());
                request.getServletContext().log("Error",e);
                response.setStatus(500);
            }
        } catch (Exception e) {
            responseJsonObject.addProperty("status", "fail");
            request.getServletContext().log("Login failed");
            responseJsonObject.addProperty("message", "reCaptcha not verified ");
            out.write(responseJsonObject.toString());
            response.setStatus(200);
            out.close();
//            return;
        }


        finally {
            out.close();
        }
    }
}
