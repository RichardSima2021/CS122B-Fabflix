package XmlParser;

public class ActorInMovie {
    private Actor actor;
    private Movie movie;

    public ActorInMovie(Actor actor, Movie movie){
        this.actor = actor;
        this.movie = movie;
    }

    public Actor getActor() {
        return actor;
    }
    public Movie getMovie() {
        return movie;
    }
    @Override
    public String toString(){
        return "" + actor.getName() + " in " + movie.getTitle();
    }

}
