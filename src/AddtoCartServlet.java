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

@WebServlet(name = "AddtoCartServlet", urlPatterns = "/api/add-to-cart")
public class AddtoCartServlet extends HttpServlet{

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();

        try{
//            System.out.println(request.getRequestURI());
//            System.out.println(request.getQueryString());
            String title = request.getParameter("title");
            double price = Double.parseDouble(request.getParameter("price"));

            HttpSession session = request.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");

//            System.out.println("Movie: " + title + " price: " + price);
            if (cart == null){
                cart = new ShoppingCart();
                session.setAttribute("cart", cart);
            }
            cart.addItem(title, 1, price);
//            System.out.println(cart);
            response.setStatus(200);
        }
        catch(Exception e){

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
            request.getServletContext().log("Error:", e);
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        finally{
            out.close();
        }


    }
}
