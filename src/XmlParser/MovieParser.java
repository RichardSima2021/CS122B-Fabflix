package XmlParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.sql.*;
/*
<movies>
    <directorfilms>
        <films>
            <film>
                <t>Title</t>
                <year>xxxx</year>
                <cats>
                    <cat></cat>
                    <cat></cat>
            <film>
        </films>
    </directorfilms>
</movies>
 */

public class MovieParser {
//    List<Movie> movies = new ArrayList<>();
//    Set<Movie> movies = new HashSet<Movie>();
//    List<String> errorMovies = new ArrayList<>();
    Document movieDoc;
    HashMap<String, String> codesToGenres;
    HashMap<String, Movie> moviesById; //movies by xml id
    HashMap<String, String> existingXMLtoMovieID; // this stores movies that exist in the DB already so we can find via XMLid
    HashMap<String, Integer> errorCounts;
    String loginUser;
    String loginPasswd;
    String loginUrl;
    Connection connection;

    public MovieParser(){
        loginUser = "mytestuser";
        loginPasswd = "My6$Password";
        loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        }
        catch(Exception e){

        }

        existingXMLtoMovieID = new HashMap<>();
        codesToGenres = new HashMap<>();
        codesToGenres.put("Susp", "Thriller");
        codesToGenres.put("CnR", "Crime");
        codesToGenres.put("Dram", "Drama");
        codesToGenres.put("West", "Western");
        codesToGenres.put("Myst", "Mystery");
        codesToGenres.put("S.F.", "Sci-Fi");
        codesToGenres.put("Advt", "Adventure");
        codesToGenres.put("Horr", "Horror");
        codesToGenres.put("Romt", "Romance");
        codesToGenres.put("Comd", "Comedy");
        codesToGenres.put("Musc", "Musical");
        codesToGenres.put("Docu", "Documentary");
        codesToGenres.put("Porn", "Adult");
        codesToGenres.put("Noir", "Black");
        codesToGenres.put("BioP", "Biography");
        codesToGenres.put("TV", "TV Show");
        codesToGenres.put("TVs", "TV series");
        codesToGenres.put("TVm", "TV miniseries");
        codesToGenres.put("Ctxx", "Uncategorized");
        codesToGenres.put("Actn", "Action");
        codesToGenres.put("Camp", "Camp");
        codesToGenres.put("Disa", "Disaster");
        codesToGenres.put("Epic", "Epic");
        codesToGenres.put("Cart", "Animation");
        codesToGenres.put("Faml", "Family");
        codesToGenres.put("Surl", "Surreal");
        codesToGenres.put("AvGa", "Avant Garde");
        codesToGenres.put("Hist", "History");
        moviesById = new HashMap<>();
        errorCounts = new HashMap<>();
    }
    public void run(){
        parseXmlFile();
        parseDocument();
//        printData();
        printReport();
    }

    public HashMap<String, Movie> getParsedMovies(){
        return moviesById;
    }

    public HashMap<String, Integer> getErrorCounts(){
        return errorCounts;
    }

    private void parseXmlFile(){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            movieDoc = documentBuilder.parse("stanford_movies/mains243.xml");
        }
        catch(ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseDocument(){
        Element documentElement = movieDoc.getDocumentElement();

        NodeList directorsList = documentElement.getElementsByTagName("directorfilms");

        for(int i = 0; i < directorsList.getLength(); i++){
            Element director = (Element) directorsList.item(i);
            try{
                String directorName = getDirectorName(director);
                NodeList movieList = director.getElementsByTagName("film");
                parseMovieList(movieList, directorName);
            }
            catch(MovieDataException e){}
        }
    }

    private void parseMovieList(NodeList movieList, String directorName){
        for(int i = 0 ; i < movieList.getLength(); i++){
            Element movieElement = (Element) movieList.item(i);
            try{
                Movie movie = parseMovie(movieElement, directorName);

                boolean inserted = insertIntoDatabase(movie);
                if(!inserted){
                    if(errorCounts.containsKey("Existing Movie"))
                    {
                        errorCounts.put("Existing Movie", errorCounts.get("Existing Movie") + 1);
                    }
                    else{
                        errorCounts.put("Existing Movie", 1);
                    }
                }
                else{
                    moviesById.put(movie.getXmlID(), movie);
                }
            }
            catch(MovieDataException e){
//                String errorStr = "Failed to parse Movie: " + e.getMovieTitle() + " | Error: " + e.getMessage() + " Field: " + e.getErroneousField() + " Value: " + e.getErroneousValue();
                if(errorCounts.containsKey(e.getMessage())){
                    errorCounts.put(e.getMessage(), errorCounts.get(e.getMessage())+1);
                }
                else{
                    errorCounts.put(e.getMessage(),1);
                }
//                errorMovies.add(errorStr);
            }
            catch(UnnamedMovieException e){
                if(errorCounts.containsKey("Unnamed Movie")){
                    errorCounts.put("Unnamed Movie", errorCounts.get("Unnamed Movie")+1);
                }
                else{
                    errorCounts.put("Unnamed Movie", 1);
                }
//                errorMovies.add("Unnamed Movie");
            }
        }
    }

    private Movie parseMovie(Element filmElement, String directorName) throws MovieDataException, UnnamedMovieException{
        String title;
        int year;
        String[] genres;
        String xmlID;
        try{
            title = getTitle(filmElement, "t");
        }
        catch(UnnamedMovieException e){
            throw e;
        }
        try{
            xmlID = getTextValue(filmElement, "fid");
            year = getYear(filmElement, "year");
            genres = getGenres(filmElement);
            return new Movie(title, year, directorName, genres, xmlID);
        }
        catch(MovieDataException e){
//            if(title.equals("Star Wars")){
//                System.out.println(title + " " + e.getMessage() + " " + e.getErroneousField() + " " + e.getErroneousValue());
//            }
            if(e.getErroneousValue().equals("2")){
                System.out.println(title + " " + e.getMessage() + " " + e.getErroneousField() + " " + e.getErroneousValue());
            }
            throw new MovieDataException(title, e.getMessage(), e.getErroneousField(), e.getErroneousValue());
        }

    }



    private String getDirectorName(Element directorElement) throws MovieDataException{
        NodeList director = directorElement.getElementsByTagName("dirname");
        try{
            String directorName = director.item(0).getFirstChild().getNodeValue();
            if(directorName == null){
                throw new MovieDataException("N/A", "Director not found", "", "");
            }
            return directorName;
        }
        catch (Exception e){
            throw new MovieDataException("N/A", "Director not found", "","");
        }
    }

    private String getTitle(Element filmElement, String tagName) throws UnnamedMovieException{
        String title = null;
        NodeList nodeList = filmElement.getElementsByTagName(tagName);
        if(nodeList.getLength() <= 0){
            throw new UnnamedMovieException("Unnamed Movie");
        }
        else{
            try{
                title = nodeList.item(0).getFirstChild().getNodeValue();
                if(title == null){
                    throw new UnnamedMovieException("Unnamed Movie");
                }
                return title;
            }
            catch(Exception e){
                throw new UnnamedMovieException("Unnamed Movie");
            }
        }
    }
    private int getYear(Element filmElement, String tagName) throws MovieDataException {
        String yearVal = getTextValue(filmElement, tagName);
        if(yearVal == null || yearVal.equals("")){
            throw new MovieDataException("","No Year Value", tagName, "null");
        }
        else{
            try{
                return Integer.parseInt(yearVal);
            }
            catch(NumberFormatException e){
                throw new MovieDataException("", "Invalid Year Value", tagName, yearVal);
            }
        }
    }

    private String[] getGenres(Element filmElement) throws MovieDataException {
        NodeList genresList = filmElement.getElementsByTagName("cat");
        ArrayList<String> genresArrList = new ArrayList<>();


//        String genres[] = new String[genresList.getLength()];
        if(genresList.getLength() <= 0 || genresList.item(0).getFirstChild() == null){
            throw new MovieDataException("","No Genres", "cats", "1");
//            String[] nullGenres = new String[1];
//            nullGenres[0] = null;
//            return nullGenres;
        }
        for(int i = 0; i < genresList.getLength(); i++){
            Element genreElement = (Element) genresList.item(i);
//            String genre = getTextValue(genreElement,"cat");
            String genre = codesToGenres.get(genreElement.getFirstChild().getNodeValue());
            if(genre != null){
                genresArrList.add(genre);
            }
        }
        if(genresArrList.size() == 0){
            throw new MovieDataException("","No Genres", "cats", "2");
        }
        String[] genres = new String[genresArrList.size()];
        for(int i = 0; i < genresArrList.size(); i++){
            genres[i] = genresArrList.get(i);
        }
        return genres;
    }

    private String getTextValue(Element element, String tagName){

        String textVal = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
//        System.out.println(tagName + " " + nodeList.getLength());
        if(nodeList.getLength() > 0){
            textVal = nodeList.item(0).getFirstChild().getNodeValue();
        }
        return textVal;
    }


    private void printData(){
        for(String id : moviesById.keySet()){
            System.out.println(id + moviesById.get(id));
        }
//        System.out.println(errorMovies.size() + " movies failed to parse");
    }

    private void printReport(){
        System.out.println("Inserted " + moviesById.size() + " movies");
        for(String error : errorCounts.keySet()){
            System.out.println(errorCounts.get(error) + " movies had " + error);
        }
    }

    public HashMap<String,String> getExistingXMLtoMovieID(){
        return existingXMLtoMovieID;
    }

    private boolean insertIntoDatabase(Movie movie){
        String movieTitle = movie.getTitle();
        String id = movie.getId();
        int year = movie.getYear();
        String director = movie.getDirector();
        String[] genres = movie.getGenres();
        int[] genreIDs = new int[genres.length];

        try{
            String findExistingMovie = "SELECT * FROM movies WHERE UPPER(title) LIKE UPPER(?) AND year = ?";
            PreparedStatement findExistingStatement = connection.prepareStatement(findExistingMovie);

            findExistingStatement.setString(1,movieTitle);
            findExistingStatement.setInt(2, year);
            ResultSet existingMovies = findExistingStatement.executeQuery();
            if(existingMovies.next()){
                existingXMLtoMovieID.put(movie.getXmlID(),existingMovies.getString("id"));
                findExistingStatement.close();
                existingMovies.close();
                return false;
            }
            findExistingStatement.close();
            existingMovies.close();

            String query = "INSERT INTO movies(id, title, year, director) VALUES(?,?,?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(query);
            insertStatement.setString(1, id);
            insertStatement.setString(2,movieTitle);
            insertStatement.setInt(3, year);
            insertStatement.setString(4, director);
            insertStatement.executeUpdate();
            insertStatement.close();

            for(int i = 0; i < genres.length; i++){
                String genre = genres[i];
//                System.out.println(genre);
                String findGenreQuery = "SELECT * FROM genres WHERE name = ?";
                PreparedStatement findGenreStatement = connection.prepareStatement(findGenreQuery);

                findGenreStatement.setString(1, genre);
                ResultSet existingGenres = findGenreStatement.executeQuery();
                if(existingGenres.next()){
                    genreIDs[i] = existingGenres.getInt("id");
                    existingGenres.close();
                    findGenreStatement.close();
                }
                else{
                    existingGenres.close();
                    String addGenreQuery = "INSERT INTO genres(name) VALUES(?)";
                    PreparedStatement addGenreStatement = connection.prepareStatement(addGenreQuery);
                    addGenreStatement.setString(1,genre);
                    addGenreStatement.executeUpdate();
                    addGenreStatement.close();

                    ResultSet getNewGenreId = findGenreStatement.executeQuery();
                    getNewGenreId.next();
                    genreIDs[i] = getNewGenreId.getInt("id");


                    getNewGenreId.close();
                    findGenreStatement.close();
                }
            }

            for(int i = 0; i < genreIDs.length; i++){
                String linkGenreAndMovieQuery = "INSERT INTO genres_in_movies(genreId, movieId) VALUES(?,?)";
                PreparedStatement linkGenreAndMovieStatement = connection.prepareStatement(linkGenreAndMovieQuery);

                linkGenreAndMovieStatement.setInt(1, genreIDs[i]);
                linkGenreAndMovieStatement.setString(2, id);

                linkGenreAndMovieStatement.executeUpdate();
            }
        }
        catch(Exception e){
//            System.out.println(movie.getGenresInStr());
//            System.out.println(movie);
            return false;
        }
        return true;
    }
    public static void main(String[] args){
        MovieParser movieParser = new MovieParser();

        movieParser.run();

    }
}