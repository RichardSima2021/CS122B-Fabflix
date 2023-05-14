package XmlParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args){
        HashMap<String, Movie> moviesById;
        HashMap<String, Actor> actorsByName;

        MovieParser movieParser = new MovieParser();
        movieParser.run();
        moviesById = movieParser.getParsedMovies();


        ActorParser actorParser = new ActorParser();
        actorParser.run();
        actorsByName = actorParser.getActors();

        CastParser castParser = new CastParser(actorsByName, moviesById);
        castParser.run();

    }
}
