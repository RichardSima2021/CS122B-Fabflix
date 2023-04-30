import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")
public class ShoppingCartServlet extends HttpServlet{

    // Create a dataSource which registered in web.xml
//    private DataSource dataSource;
//
//    public void init(ServletConfig config) {
//        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
//        } catch (NamingException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

//        System.out.println(request.getRequestURI() + request.getQueryString());
//        System.out.println("debug");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try{
            // Get a connection from dataSource

            HttpSession session = request.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
            if (cart == null){
                cart = new ShoppingCart();
                session.setAttribute("cart", cart);
            }
//            System.out.println(cart);
            JsonObject shoppingCartJson = new JsonObject();
            /*
                shoppingCartJson = {items:[[title, count, price/unit, total],[title, count, price/unit, total]], total: cart.getTotal}
             */
            JsonArray itemsArray = new JsonArray();
            List<CartItem> items = cart.getItems();

            for(CartItem item : items){
                JsonArray itemInfo = new JsonArray();
                itemInfo.add(item.getItemName());
                itemInfo.add(item.getQuantity());
                itemInfo.add(item.getPrice());
                itemInfo.add(Math.round(item.getSubtotal()*100.0)/100.0);
                itemsArray.add(itemInfo);
            }
            shoppingCartJson.add("items",itemsArray);
            shoppingCartJson.addProperty("total", Math.round(cart.getTotal()*100.0)/100.0);
            System.out.println(shoppingCartJson);

            // Write JSON string to output
//            out.write(genreJson.toString());
            out.write(shoppingCartJson.toString());
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
