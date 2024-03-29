/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.authentication.jwt;

import java.io.Serializable;

/**
 * Authentication Response DTO.
 * <pre>
 *     JSON: {"token":"&lt;Token String&gt;"}
 * </pre>
 */
public class JwtAuthenticationResponse implements Serializable {

    private final String token;

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
