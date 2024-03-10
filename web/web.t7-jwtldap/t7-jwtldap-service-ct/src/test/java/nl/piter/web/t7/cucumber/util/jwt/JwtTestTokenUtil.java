/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.cucumber.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;

/**
 * Custom JwtToken Util for testing purposes.
 */
public class JwtTestTokenUtil {

    private final Key secretKey;

    public JwtTestTokenUtil(String key) {
        try {
            this.secretKey = createKey(key);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private Key createKey(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
        Mac sha512Hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA512");
        sha512Hmac.init(secretKey);
        return secretKey;
    }

    public boolean isValid(String token) {
        return this.getUsernameFromToken(token) != null;
    }

    public String getUsernameFromToken(String token) {
        final Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    public Date getIssuedDateFromToken(String token) {
        final Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return new Date((Long) claims.get(Claims.ISSUED_AT));
    }

    public Date getExpirationDateFromToken(String token) {
        final Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        return claims.getExpiration();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public String toString(JwtTestToken jwtTestToken) {
        StringBuilder sb = new StringBuilder();
        sb.append("Token:[");
        Claims claims = this.getClaimsFromToken(jwtTestToken.token);
        Iterator<String> keyIterator = claims.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = keyIterator.next();
            Object value = claims.get(key);
            sb.append(String.format("%s: '%s'", key, value));
            if (keyIterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
