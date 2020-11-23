package be.heh.pfa;

public class User {
    private int id;
    private String name;
    private String firstname;
    private String email;
    private String password;
    private String role;
    private String perm;

    public User() {

    }

    public User(int id, String name, String firstname, String email, String password, String role, String perm) {
        this.id = id;
        this.name = name;
        this.firstname = firstname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.setPerm(perm);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

    public String getFistname() {
        return firstname;
    }

    public void setFirstname(String username) { this.firstname = username; }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String username) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPerm() {
        return perm;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }

    @Override
    public String toString() {
        String str = "Je m'appelle " + getName() + " " + getFistname()
                + ", mon adresse mail est : " + getEmail()
                + ", J'ai le rôle : " + getRole()
                + " et j'ai la permission : " + getPerm();
        return  str;
    }
}
