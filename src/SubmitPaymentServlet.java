import com.google.gson.JsonArray;
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
import java.time.LocalDate;

@WebServlet(name = "SubmitPaymentServlet", urlPatterns = "/api/submit-payment")
public class SubmitPaymentServlet extends HttpServlet {
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

        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null){
            cart = new ShoppingCart();
            session.setAttribute("cart", cart);
        }
        User currentUser = (User) session.getAttribute("user");
        int customerID = currentUser.getId();


        JsonObject responseJsonObject = new JsonObject();

        try (Connection conn = dataSource.getConnection()) {

            Statement getUserStatement = conn.createStatement();

//            select * from creditcards where id = 960 and firstNmae = "t" and lastName = "t" and expiration = DATE("2005-11-20");
            String query = "SELECT * FROM creditcards WHERE firstName = \"" + firstName + "\" and lastName = \"" + lastName + "\" and id = \"" + cardNumber + "\" and expiration = DATE(\"" + expiration + "\")";
            ResultSet rs = getUserStatement.executeQuery(query);
//            System.out.println(rs);
            if(cart.isEmpty()){
                System.out.println("Cart is empty");
                responseJsonObject.addProperty("status","fail");
                responseJsonObject.addProperty("message", "Cart is empty");
            }
            else if(rs.next() == false){
                // no such user
                System.out.println("payment failed");
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Payment failed");
                responseJsonObject.addProperty("message", "Incorrect Payment Information");
            }
            else{
                // existing user and correct password
                // This is the only place we refer to it as user vs email because this is stored to session
//                    request.getSession().setAttribute("user", new User(email));
//                System.out.println("payment success");
                JsonArray transactionArray = new JsonArray();
                for(CartItem item : cart.getItems()){
                    JsonObject transactionInfo = new JsonObject();
                    String movieTitle = item.getItemName();
                    int copies = item.getQuantity();
                    Statement getMovieIdStatement = conn.createStatement();
                    String getMovieIdQuery = "SELECT id FROM movies WHERE title = \"" + movieTitle + "\"";
                    ResultSet movieIdResult = getMovieIdStatement.executeQuery(getMovieIdQuery);
                    movieIdResult.next();
                    String movieId = movieIdResult.getString("id");
                    LocalDate checkoutDate = LocalDate.now();
//                    System.out.println("Checked out " + copies + " copies of " + movieTitle + " with movie Id " + movieId + " by " + customerID + " on " + checkoutDate);
                    getMovieIdStatement.close();
                    movieIdResult.close();

                    Statement insertIntoSalesStatement = conn.createStatement();
                    String insertIntoSalesQuery = "INSERT INTO sales (customerId, movieId, saleDate, copies) VALUES (" + customerID + ", \"" + movieId + "\", \"" + checkoutDate + "\", " + copies + ")";
//                    System.out.println(insertIntoSalesQuery);
                    insertIntoSalesStatement.executeUpdate(insertIntoSalesQuery);
                    insertIntoSalesStatement.close();

                    Statement getSaleIDStatement = conn.createStatement();
                    String getSaleIDQuery = "SELECT id FROM sales ORDER BY id DESC LIMIT 1";
                    ResultSet saleIDResult = getSaleIDStatement.executeQuery(getSaleIDQuery);
                    saleIDResult.next();
                    int saleID = saleIDResult.getInt("id");

                    transactionInfo.addProperty("id", saleID);
                    transactionInfo.addProperty("count", copies);
                    transactionInfo.addProperty("title", movieTitle);
                    transactionInfo.addProperty("pricePerUnit",item.getPrice());
                    transactionInfo.addProperty("movieId", movieId);
                    transactionArray.add(transactionInfo);
                    getSaleIDStatement.close();
                    saleIDResult.close();
                }
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                responseJsonObject.add("transactionList",transactionArray);
                cart.clear();
//                System.out.println("submit payment success");
            }
            getUserStatement.close();
            rs.close();
//            System.out.println(responseJsonObject);
            // Write JSON string to output
//            out.write(genreJson.toString());
            response.getWriter().write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        }catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            System.out.println(jsonObject);
            response.getWriter().write(responseJsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally{
            response.getWriter().close();
        }
//        response.getWriter().write(responseJsonObject.toString());
    }
}
