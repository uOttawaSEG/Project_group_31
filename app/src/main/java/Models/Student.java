public class Student{
    private String firstName;

    //Constructor to create student object with first name
    public Student(String firstName) {
        this.firstName = firstName;
    }
    //Get the first name
    public String getFirstName() {
        return firstName;
    }
    //Set or update the first name
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    private String LastName;
    //Constructor to create student object with last name
    public Student(String LastName) {
        this.LastName = LastName;
    }
    //Get the last name
    public String getLastName() {
        return LastName;
    }
    //Set or update the last name
    public void setLastName(String LastName) {
        this.LastName = LastName;
    }
}