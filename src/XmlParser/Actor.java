package XmlParser;

public class Actor {
    private String name;
    private int year;
    public static int previousID = 9423082;
    private String id;
    public Actor(String name, int year){
        this.name = name;
        this.year = year;
        previousID += 1;
        id = "nm" + previousID;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }
    public String getId(){
        return id;
    }

    @Override
    public String toString() {
        String res = "";
        res += "Name: " + name;
        if(year == -1){
            res += " | Year: null";
        }
        else{
            res += " | Year: " + year;
        }
        return res;
    }
}
