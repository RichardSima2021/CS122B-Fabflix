import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.sql.*;
import java.io.IOException;

@WebServlet(name = "DashboardServlet", urlPatterns = "/api/get-database-info")
public class DashboardServlet extends HttpServlet{
    private DataSource dataSource;

    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }
        catch (NamingException e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        JsonArray responseJsonArray = new JsonArray();
        PrintWriter out = response.getWriter();

        try(Connection conn = dataSource.getConnection()){
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet tables = dbmd.getTables("moviedb", null, null, new String[]{"TABLE"});
            while(tables.next()){
                JsonObject currentTable = new JsonObject();
                String tableName = tables.getString("TABLE_NAME");
                if(tableName.equals("customers_backup")){
                    continue;
                }
                currentTable.add(tableName, getTableData(conn, dbmd, tableName));
                responseJsonArray.add(currentTable);
            }

//            printJsonArray(responseJsonArray);

            tables.close();
            out.write(responseJsonArray.toString());
            response.setStatus(200);
        }
        catch(Exception e){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage",e.getMessage());
            System.out.println(jsonObject);
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        finally{
            out.close();
        }
    }

    private JsonObject getTableData(Connection conn, DatabaseMetaData dbmd, String tableName) throws SQLException {
        ResultSet columns = dbmd.getColumns(null, null, tableName, null);
        JsonObject columnsInTable = new JsonObject();
        while(columns.next()){
            String colName = columns.getString("COLUMN_NAME");
            String colType = columns.getString("TYPE_NAME");
            columnsInTable.addProperty(colName, colType);
        }
//        System.out.println(columnsInTable.toString());
        return columnsInTable;
    }

    private void printJsonArray(JsonArray JSR){
        for(JsonElement jsonElement : JSR){
            System.out.println(jsonElement);
        }
    }

}
