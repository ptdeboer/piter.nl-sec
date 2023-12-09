package nl.piter.web.t7.cucumber.util.jwt;

public class JwtTestAuthRequest {
    public String username;
    public String password;
    public JwtTestAuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
