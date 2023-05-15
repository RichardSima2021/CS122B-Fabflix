//package XmlParser;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//
//public class Main {
//
//    public static void main(String[] args){
//        HashMap<String, Movie> moviesById;
//        HashMap<String, Actor> actorsByName;
//        HashMap<String, String> existingMovies;
//        HashMap<String, String> existingActors;
//
//        MovieParser movieParser = new MovieParser();
//        movieParser.run();
//        moviesById = movieParser.getParsedMovies();
//        existingMovies = movieParser.getExistingXMLtoMovieID();
//
//
//        ActorParser actorParser = new ActorParser();
//        actorParser.run();
//        actorsByName = actorParser.getActors();
//        existingActors = actorParser.getExistingActorsByName();
//
//        CastParser castParser = new CastParser(actorsByName, moviesById, existingMovies, existingActors);
//        castParser.run();
//
//    }
//}
