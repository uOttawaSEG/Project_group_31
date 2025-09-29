
public class Student {
    private String firstName;
    private String lastName;
    private String email;  //Username
    private String password;
    private String phoneNumber;
    private String programOfStudy;

    //Constructor to create student object with first name and last name
    public Student(String firstName, String lastName, String email, String password, String phoneNumber, String programOfStudy) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.programOfStudy = programOfStudy;
    }
    //Get the first name
    public String getFirstName() {

    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    // Get the last name
    public String getLastName() {
        return lastName;
    }
    // Set or update the last name
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}