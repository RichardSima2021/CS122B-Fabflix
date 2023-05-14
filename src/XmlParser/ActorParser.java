package XmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class ActorParser {
//    List<Actor> actors = new ArrayList<>();
    HashMap<String,Actor> actorsByName = new HashMap<String, Actor>();
    List<String> errorActors = new ArrayList<>();
    Document actorDocument;

    String loginUser;
    String loginPasswd;
    String loginUrl;
    Connection connection;

    public void run(){
        parseXmlFile();
        parseDocument();
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
    }

    private void parseXmlFile(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            actorDocument = documentBuilder.parse("actors63.xml");

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
            insertIntoDatabase(actor);
            actorsByName.put(actor.getName(), actor);
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

    private void insertIntoDatabase(Actor actor){

    }

    public void printReport(){
        System.out.println("Inserted " + actorsByName.size() + " actors");
    }
    public HashMap<String,Actor> getActors(){
        return actorsByName;
    }

//    public static void main(String[] args){
//        ActorParser actorParser = new ActorParser();
//        actorParser.run();
//
//        HashMap<String, Actor> actorsByName = actorParser.getActors();
//
//        Iterator actorIterator = actorsByName.entrySet().iterator();
//        while(actorIterator.hasNext()){
//            Map.Entry actorEntry = (Map.Entry) actorIterator.next();
//            Actor actor = (Actor) actorEntry.getValue();
//            System.out.println(actor);
//        }
//
//
//        System.out.println("Parsed: " + actorsByName.size() + " actors");
//    }
}
