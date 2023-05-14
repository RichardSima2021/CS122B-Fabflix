package XmlParser;

public class MovieDataException extends Exception{
    private String erroneousField;
    private String erroneousValue;
    private String movieTitle;
    public MovieDataException(String movieTitle, String errorMessage, String erroneousField, String erroneousValue){
        super(errorMessage);
        this.movieTitle = movieTitle;
        this.erroneousField = erroneousField;
        this.erroneousValue = erroneousValue;
    }

    public String getMovieTitle() {
        return movieTitle;
    }
    public String getErroneousField() {
        return erroneousField;
    }

    public String getErroneousValue() {
        return erroneousValue;
    }
}
