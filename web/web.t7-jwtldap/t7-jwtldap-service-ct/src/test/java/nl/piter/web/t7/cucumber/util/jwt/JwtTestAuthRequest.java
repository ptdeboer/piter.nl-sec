/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.cucumber.util.jwt;

public class JwtTestAuthRequest {
    public String username;
    public String password;

    public JwtTestAuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
