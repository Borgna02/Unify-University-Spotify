package unify.nullpointerexception.domain;

public class User {

    private Integer id;
    private String username;
    private String email;
    private String password;
    private boolean isAdmin;

    public User(Integer id, String username, String email, String password, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(Boolean value) {
        this.isAdmin = value;
    }

    public Integer getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String toString() {
        String tip = "";
        if (this.isAdmin) {
            tip = "Amministratore";
        } else {
            tip = "Utente";
        }
        return id + " - " + username + " - " + email + " - " + tip;
    }

    public Boolean isAdmin() {
        return this.isAdmin;
    }

    public boolean equals(User other) {
        return this.id.equals(other.id);
    }

}
