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

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
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
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String cardNumber = request.getParameter("card_number");
        String expiration = request.getParameter("expiration");




        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {
            Statement getUserStatement = conn.createStatement();

//            select * from creditcards where id = 960 and firstNmae = "t" and lastName = "t" and expiration = DATE("2005-11-20");
            String query = "SELECT * FROM creditcards WHERE firstName = \"" + firstName + "\" and lastName = \"" + lastName + "\" and id = \"" + cardNumber + "\" and expiration = DATE(\"" + expiration + "\")";
            ResultSet rs = getUserStatement.executeQuery(query);
            if(rs.next() == false){
                // no such user
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "Incorrect Payment Information");
            }
            else{
                // existing user and correct password
                // This is the only place we refer to it as user vs email because this is stored to session
//                    request.getSession().setAttribute("user", new User(email));
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
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
        response.getWriter().write(responseJsonObject.toString());
    }
}
