public class Employee {
    private String email;
    private String fullName;

    public Employee(String email, String fullName) {
        this.email = email;
        this.fullName = fullName;
    }

    public String getEmail(){
        return email;
    }

    public String getFullName(){
        return fullName;
    }
}
