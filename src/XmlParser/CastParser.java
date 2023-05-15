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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
public class CastParser {
//    private List<ActorInMovie> actorsInMovies = new ArrayList<>();

    private HashMap<String, ArrayList<String>> moviesAndActorIDs = new HashMap<String, ArrayList<String>>();
    private Document castsDocument;
    private HashMap<String, Actor> actors;
    private HashMap<String, Movie> movies;
    private HashMap<String, Integer> errorCount;
    private HashMap<String, String> existingXMLtoMovieID;
    private HashMap<String, String> existingActors;
    private int actorsAdded = 1;
    private int moviesAddedInto = 1;

    String loginUser;
    String loginPasswd;
    String loginUrl;
    Connection connection;

    public CastParser(HashMap<String,Actor> actors, HashMap<String, Movie> movies, HashMap<String, String> existingMovies, HashMap<String, String> existingActors){
        this.actors = actors;
        this.movies = movies;
        this.errorCount = new HashMap<>();
        loginUser = "mytestuser";
        loginPasswd = "My6$Password";
        loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        }
        catch(Exception e){

        }
        this.existingXMLtoMovieID = existingMovies;
        this.existingActors = existingMovies;
    }

    public CastParser() {

    }

    public void run(){
        parseXmlFile();
        parseDocument();
//        System.out.println(actorsAdded + " actors added");
        insertIntoDB();
        printReport();
    }

    private void parseXmlFile(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            // parse using builder to get DOM representation of the XML file
            castsDocument = documentBuilder.parse("stanford_movies/casts124.xml");
        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument(){
        Element documentElement = castsDocument.getDocumentElement();
        NodeList directorList = documentElement.getElementsByTagName("dirfilms");
        for(int i = 0; i < directorList.getLength(); i++){
            Element director = (Element) directorList.item(i);
            parseDirector(director);
        }
    }

    private void parseDirector(Element directorElement){
        NodeList filmsList = directorElement.getElementsByTagName("m");
        for(int i = 0; i < filmsList.getLength(); i++){
            Element film = (Element) filmsList.item(i);
            parseFilm(film);
        }
    }

    private void parseFilm(Element filmElement){
        String filmXMLID = null;
        String actorName = null;
        NodeList filmIDNode = filmElement.getElementsByTagName("f");
        if(filmIDNode.getLength() > 0){
            filmXMLID = filmIDNode.item(0).getFirstChild().getNodeValue();
        }
        NodeList actorNameNode = filmElement.getElementsByTagName("a");
        try{
            actorName = actorNameNode.item(0).getFirstChild().getNodeValue();
        }
        catch(Exception e){
            try{
                NodeList roleNode = filmElement.getElementsByTagName("r");
//                System.out.println("No actor recorded for role " + roleNode.item(0).getFirstChild().getNodeValue());
                if(errorCount.containsKey("No actors for a role")){
                    errorCount.put("No actors for a role", errorCount.get("No actors for a role")+1);
                }
                else{
                    errorCount.put("No actors for a role", 1);
                }
            }
            catch(Exception e2){
//                System.out.println("No information about an actor for film " + filmID);
                if(errorCount.containsKey("No information about actor")){
                    errorCount.put("No information about actor", errorCount.get("No information about actor")+1);
                }
                else{
                    errorCount.put("No information about actor", 1);
                }
            }
        }

        // try to find actor in parsed hashmap
        Actor actor = actors.get(actorName);

        String actorId = null;
        if(actor == null){
            String existingActorId = existingActors.get(actorName);
            if(existingActorId == null){
                if(errorCount.containsKey("Actor not provided for movie")){
                    errorCount.put("Actor not provided for movie", errorCount.get("Actor not provided for movie")+1);
                }
                else{
                    errorCount.put("Actor not provided for movie", 1);
                }
            }
            else{
                actorId = existingActorId;
            }
        }
        else{
            actorId = actor.getId();
        }


//        String actorId = actors.get(actorName).getId();

        Movie movie = movies.get(filmXMLID);
        String movieXmlId = null;
        if(movie == null){
            if(!existingXMLtoMovieID.containsKey(filmXMLID)){
                if(errorCount.containsKey("Movies not provided for an actor")){
                    errorCount.put("Movies not provided for an actor", errorCount.get("Movies not provided for an actor")+1);
                }
                else{
                    errorCount.put("Movies not provided for an actor", 1);
                }
            }
            else{
                movieXmlId = filmXMLID;
            }
        }
        else{
            movieXmlId = movie.getXmlID();
        }

//        if(filmXMLID.equals("GgL3")){
//            System.out.println("Found GgL3");
//            System.out.println("Actor " + actorName);
//            System.out.println(actor);
//            System.out.println(actorId);
//            System.out.println(movie);
//        }

        if(movieXmlId != null && actorId != null){
            if(!moviesAndActorIDs.containsKey(filmXMLID)){
                moviesAndActorIDs.put(filmXMLID, new ArrayList<String>());
            }
            moviesAndActorIDs.get(filmXMLID).add(actorId);

//            if(filmXMLID.equals("GgL3")){
//                System.out.println(actorId + " in " + movie.getId());
//            }
        }
//        System.out.println(actorName + " in " + filmID);
    }

    private void insertIntoDB(){
        String insertStarInMovieQuery = "INSERT INTO stars_in_movies VALUES(?,?)";

//        try{

            for(String movieId : moviesAndActorIDs.keySet()){
                boolean starWars = false;
//                if(movieId.equals("GgL3")){
//                    starWars = true;
//                }

                String movieDbId;
                if(movies.containsKey(movieId)){
                    // if we can find this xml id in the new movies
                    movieDbId = movies.get(movieId).getId();
                }
                else{
                    // if can't find that xml id in the list of newly parsed movies
                    if(!existingXMLtoMovieID.containsKey(movieId)){
                        // this movie doesn't exist within the database anywhere, can't link stars to id
                        continue;
                    }
                    else{
                        // it already exists in the db
                        movieDbId = existingXMLtoMovieID.get(movieId);

                    }
//                    if(movieId.equals("GyM35")){
//                        System.out.println("debug " + movieDbId);
//                    }
                }

//                if(movieId.equals("GyM35")){
//                    System.out.println("GyM35 " + movieDbId);
//                }
//                System.out.println(movieDbId);
                try{
                    PreparedStatement insertStarInMovieStatement = connection.prepareStatement(insertStarInMovieQuery);
                    connection.setAutoCommit(false);

                    for(String actorId : moviesAndActorIDs.get(movieId)){

                        insertStarInMovieStatement.setString(1, actorId);
                        insertStarInMovieStatement.setString(2, movieDbId);
                        insertStarInMovieStatement.addBatch();
//                        if(starWars == true){
//                            System.out.println(insertStarInMovieStatement);
//                        }
//                    System.out.println(insertStarInMovieStatement);
                    actorsAdded += 1;
                    }
//                if(starWars == true){
//                    System.out.println(insertStarInMovieStatement);
//                }
                    insertStarInMovieStatement.executeLargeBatch();
                    connection.commit();

                    moviesAddedInto += 1;
                    insertStarInMovieStatement.close();
                }
                catch(SQLException e){
//                    System.out.println(e.getMessage());
                }

            }


//        }
//        catch(SQLException e){
//            System.out.println(e.getMessage());
//        }

    }

    public void printReport(){
        System.out.println(actorsAdded + " actors added in " + moviesAddedInto + " movies" );
        for(String error : errorCount.keySet()){
            System.out.println(error + ": " + errorCount.get(error));
        }
    }
    public static void main(String[] args){
        CastParser castParser = new CastParser();
        castParser.run();
    }
}
