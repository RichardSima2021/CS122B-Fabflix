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

@WebServlet(name = "ModifyCartServlet", urlPatterns = "/api/modify-cart")
public class ModifyCartServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        PrintWriter out = response.getWriter();

//        System.out.println(request.getRequestURI() + request.getQueryString());
        try{
            HttpSession session = request.getSession();
            ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
            String modification = request.getParameter("modify");
            String title = request.getParameter("title");
            if(modification.equals("minus")){
                // subtract one from cart
                cart.modifyItemCount(title,-1);
            }
            else if(modification.equals("add")){
                // add one to cart
                cart.modifyItemCount(title, 1);
            }
            else{
                // remove from cart
                cart.removeItem(title);
            }
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

