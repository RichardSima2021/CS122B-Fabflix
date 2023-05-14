package XmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
public class CastParser {
//    private List<ActorInMovie> actorsInMovies = new ArrayList<>();

    private HashMap<String, ArrayList<Actor>> moviesAndActors= new HashMap<String, ArrayList<Actor>>();
    private Document castsDocument;
    private HashMap<String, Actor> actors;
    private HashMap<String, Movie> movies;
    private HashMap<String, Integer> errorCount;

    public static int actorsAdded = 1;

    public CastParser(HashMap<String,Actor> actors, HashMap<String, Movie> movies){
        this.actors = actors;
        this.movies = movies;
        this.errorCount = new HashMap<>();
    }

    public CastParser() {

    }

    public void run(){
        parseXmlFile();
        parseDocument();
//        System.out.println(actorsAdded + " actors added");
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
        String filmID = null;
        String actorName = null;
        NodeList filmIDNode = filmElement.getElementsByTagName("f");
        if(filmIDNode.getLength() > 0){
            filmID = filmIDNode.item(0).getFirstChild().getNodeValue();
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

        Movie movie = movies.get(filmID);

        if(movie == null){
//            System.out.println("Movie with ID: " + filmID + " not found");
            if(errorCount.containsKey("Movies not provided for an actor")){
                errorCount.put("Movies not provided for an actor", errorCount.get("Movies not provided for an actor")+1);
            }
            else{
                errorCount.put("Movies not provided for an actor", 1);
            }
        }
        else if(actor == null){
//            System.out.println("Actor " + actorName + " for movie " + movie.getTitle() + " not found in actors database");
            if(errorCount.containsKey("Actor not provided for movie")){
                errorCount.put("Actor not provided for movie", errorCount.get("Actor not provided for movie")+1);
            }
            else{
                errorCount.put("Actor not provided for movie", 1);
            }
        }
        else{
//            actorsInMovies.add(new ActorInMovie(actor, movie));
            actorsAdded += 1;
            if(!moviesAndActors.containsKey(filmID)){
                moviesAndActors.put(filmID, new ArrayList<Actor>());
            }
            moviesAndActors.get(filmID).add(actor);
        }
//        System.out.println(actorName + " in " + filmID);
    }


    public void printReport(){
        System.out.println(actorsAdded + " actors added in " + moviesAndActors.keySet().size() + " movies" );
        for(String error : errorCount.keySet()){
            System.out.println(error + ": " + errorCount.get(error));
        }
    }
    public static void main(String[] args){
        CastParser castParser = new CastParser();
        castParser.run();
    }
}
