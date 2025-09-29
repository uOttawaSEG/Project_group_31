public class Student {
    private String firstName;
    private String lastName;

    //Constructor to create student object with first name and last name
    public Student(String firstName, String lastName) {

        this.firstName = firstName;
        this.lastName = lastName;
    }
    //Get the first name
    public String getFirstName() {
        return firstName;
    }
    //Set or update the first name
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    // Get the last name
    public String getLastName() {
        return lastName;
    }
    // Set or update the last name
    public void setLastName(String lastName) {
        this.lastName = lastName;.
    }
}