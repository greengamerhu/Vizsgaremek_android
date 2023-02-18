package hu.petrik.vizsgaremek;

public class Users {
    String email;
    String fullName;
    String password;
    String repassword;

    public Users(String email, String fullName, String password, String rePassword) {
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.repassword = rePassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }
}
