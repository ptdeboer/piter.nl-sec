/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.cucumber.util.jwt;

public class JwtTestAuthRequest {
    public String username;
    public String password;

    public JwtTestAuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
