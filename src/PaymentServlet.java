import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
//    private DataSource dataSource;
//
//    public void init(ServletConfig config) {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();

        try {
            JsonObject responseJsonObject = new JsonObject();

            HttpSession session = request.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
            System.out.println(cart);
            responseJsonObject.addProperty("total", Math.round(cart.getTotal()*100.0)/100.0);
            System.out.println(responseJsonObject);



            // Write JSON string to output
//            out.write(genreJson.toString());
            out.write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        }catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
        }
    }
}
