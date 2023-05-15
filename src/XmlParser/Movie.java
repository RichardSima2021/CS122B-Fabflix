package XmlParser;

public class Movie {
    private String title;
    public static int previousID = 499470;
    private String xmlID;
    private String id;
    private int year;
    private String director;
    private String[] genres;

    public Movie(String title, int year, String director, String[] genres, String xmlID){
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = new String[genres.length];
        for(int i = 0; i < genres.length; i++){
            this.genres[i] = genres[i];
        }
        previousID += 1;
        if(previousID < 1000000){
            this.id = "tt0" + previousID;
        }
        else{
            this.id = "tt" + previousID;
        }
        this.xmlID = xmlID;
    }

    public void setDBId(String id){
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public int getYear(){
        return year;
    }
    public String getId(){
        return id;
    }
    public String getDirector(){
        return director;
    }
    public String[] getGenres(){
        return genres;
    }
    public String getGenresInStr(){
        String res = "";
        for(int i = 0; i < genres.length - 1; i++){
            res += genres[i] + ", ";
        }
        res += genres[genres.length-1];
        return res;
    }
    public String getXmlID(){
        return xmlID;
    }
    @Override
    public String toString() {
        String res = "Movie---------------\n";
        res += "Title: " + getTitle() + " ";
        res += "XmlID: " + getXmlID() + " ";
        res += "Year: " + getYear() + " ";
        res += "Director: " + getDirector() + " ";
        res += "MovieID: " + getId() + " ";
        res += "Genres: " + getGenresInStr() + "";
//        res += "--------------------";
        return res;
    }
}
