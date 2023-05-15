package XmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ActorParser {
//    List<Actor> actors = new ArrayList<>();
    HashMap<String,Actor> actorsByName = new HashMap<String, Actor>();
    int duplicateActors;
    Document actorDocument;
    HashMap<String, String> existingActorsByName;
    String loginUser;
    String loginPasswd;
    String loginUrl;
    Connection connection;

    int addedActors;
    public void run(){
        parseXmlFile();
        parseDocument();
        insertIntoDb();
        printReport();
    }

    public ActorParser(){
        loginUser = "mytestuser";
        loginPasswd = "My6$Password";
        loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        }
        catch(Exception e){

        }
        existingActorsByName = new HashMap<>();
        addedActors = 0;
    }

    private void parseXmlFile(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            actorDocument = documentBuilder.parse("stanford_movies/actors63.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument(){
        Element documentElement = actorDocument.getDocumentElement();
        NodeList actorList = documentElement.getElementsByTagName("actor");
        for(int i = 0; i < actorList.getLength(); i++){
            Element actorElement = (Element) actorList.item(i);

            Actor actor = parseActor(actorElement);
            actorsByName.put(actor.getName(), actor);
//            try{
////                System.out.println("Trying to insert " + actor);
//                boolean inserted = insertIntoDatabase(actor);
//                if(inserted){
//
////                    System.out.println("Inserted " + actor);
//                }
//                else{
//                    duplicateActors += 1;
//                }
//            }
//            catch(Exception e){
////                System.out.println(e.getMessage());
//            }

        }
    }

    private Actor parseActor(Element actorElement){
        String name = getName(actorElement);
        int birthYear = getBirthYear(actorElement);

        return new Actor(name, birthYear);
    }

    private String getName(Element actorElement){
        String nameVal = null;
        NodeList nodeList = actorElement.getElementsByTagName("stagename");
        if(nodeList.getLength() > 0){
            nameVal = nodeList.item(0).getFirstChild().getNodeValue();
        }
        return nameVal;
    }

    private int getBirthYear(Element actorElement){
        String yearVal = null;
        NodeList nodeList = actorElement.getElementsByTagName("dob");
        if(nodeList.getLength() > 0){
            try{
                yearVal = nodeList.item(0).getFirstChild().getNodeValue();
            }
            catch(Exception e){
                return -1;
            }
        }
        try{
            return Integer.parseInt(yearVal);
        }
        catch(Exception e){
            return -1;
        }
    }

    public HashMap<String, String> getExistingActorsByName(){
        return existingActorsByName;
    }

    private void insertIntoDb(){
        String query = "INSERT INTO stars VALUES(?,?,?)";
        ArrayList<Actor> actors = new ArrayList<>();
        for(String name : actorsByName.keySet()){
            actors.add(actorsByName.get(name));
        }

        try{
            connection.setAutoCommit(false);
            PreparedStatement insertStatement = connection.prepareStatement(query);

            for(Actor a : actors){
                String actorId = a.getId();
                String actorName = a.getName();
                int actorBirthYear = a.getYear();
//                String findExistingActorName = "SELECT id FROM stars WHERE UPPER(name) LIKE UPPER(?)";
//                PreparedStatement findExistingNameStatement = connection.prepareStatement(findExistingActorName);
//                findExistingNameStatement.setString(1, a.getName());
//                ResultSet sameName = findExistingNameStatement.executeQuery();
//
//                if(sameName.next()){
//                    existingActorsByName.put(a.getName(), sameName.getString("id"));
//                    duplicateActors += 1;
//                    continue;
//                }
//
//                findExistingNameStatement.close();
//                sameName.close();

                insertStatement.setString(1, actorId);
                insertStatement.setString(2, actorName);

                if(actorBirthYear < 0){
                    insertStatement.setNull(3, Types.NULL);
                }
                else{
                    insertStatement.setInt(3, actorBirthYear);
                }
                insertStatement.addBatch();
                addedActors += 1;

            }
            insertStatement.executeLargeBatch();
            insertStatement.close();
            connection.commit();
        }
        catch(SQLException e){

        }
    }

    private boolean insertIntoDatabase(Actor actor) throws SQLException{
        String actorName = actor.getName();
        String actorID = actor.getId();
        int birthYear = actor.getYear();
        try{
            String findExistingActorName = "SELECT * FROM stars WHERE UPPER(name) LIKE UPPER(?)";
            PreparedStatement findExistingNameStatement = connection.prepareStatement(findExistingActorName);
            findExistingNameStatement.setString(1, actorName);
            ResultSet sameName = findExistingNameStatement.executeQuery();

            if(sameName.next()){
                String findExistingActor = "SELECT * FROM stars WHERE UPPER(name) LIKE UPPER(?) AND (birthYear = NULL OR birthYear = ?)";
                PreparedStatement findExistingStatement = connection.prepareStatement(findExistingActor);
                findExistingStatement.setString(1, actorName);
                findExistingStatement.setInt(2, birthYear);
                ResultSet existingActors = findExistingStatement.executeQuery();

                if(existingActors.next()){
                    existingActorsByName.put(actorName, existingActors.getString("id"));
                    findExistingStatement.close();
                    existingActors.close();
                    return false;
                }
            }
            else{
                findExistingNameStatement.close();
                sameName.close();
            }
            String insertQuery = "INSERT INTO stars VALUES(?,?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setString(1, actorID);
            insertStatement.setString(2, actorName);
            if(birthYear < 0){
                insertStatement.setNull(3, Types.NULL);
            }
            else{
                insertStatement.setInt(3, birthYear);
            }
            insertStatement.executeUpdate();
            insertStatement.close();
            return true;
        }
        catch(SQLException e){
            throw e;
        }
    }

    public void printReport(){
        System.out.println("Inserted " + addedActors + " actors");
        System.out.println(duplicateActors + " existing actors");
    }
    public HashMap<String,Actor> getActors(){
        return actorsByName;
    }


    public static void main(String[] args){
        ActorParser actorParser = new ActorParser();
        actorParser.run();
    }
}
