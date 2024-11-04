package unipiloto.edu.co.prio;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String password;
    private int role;
    private int locality;

    public User(int id, String firstName, String lastName, int age, String email, String password, int role, int locality) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.password = password;
        this.role = role;
        this.locality = locality;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getLocality() {
        return locality;
    }

    public void setLocality(int locality) {
        this.locality = locality;
    }
}
