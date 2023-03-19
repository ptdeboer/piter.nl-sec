/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.authentication.user.T7AppUser;
import nl.piter.web.t7.config.JwtConfig;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static nl.piter.web.t7.config.JwtConfig.BEARER_PREFIX;
import static nl.piter.web.t7.config.JwtConfig.BEARER_PREFIX_WHITESPACE;

/**
 * Updated JwtTokenUtil.
 * Now throws exceptions unless are explicit 'validate' methods are called which should return
 * a boolean in case of invalid credentials i.s.o throw an (unchecked) JwtException.
 */
@Slf4j
public class JwtTokenUtil implements Serializable {

    private final JwtConfig jwtConfig;
    private final Key secretKey;

    public JwtTokenUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        try {
            this.secretKey = createKey(jwtConfig.getSecret());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SecurityException(e.getMessage(), e);
        }
    }

    public JwtConfig getJwtConfig() {
        return this.jwtConfig;
    }

    public String filterTokenFromHeader(String authTokenHeader) {
        if (authTokenHeader.startsWith(BEARER_PREFIX_WHITESPACE)) {
            return authTokenHeader.substring(BEARER_PREFIX_WHITESPACE.length());
        } else {
            log.debug("Token missing from header or prefix '{}'  missing:'{}'.", BEARER_PREFIX, authTokenHeader);
            return null;
        }
    }

    private Key createKey(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
        Mac sha512Hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA512");
        sha512Hmac.init(secretKey);
        return secretKey;
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

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + jwtConfig.getExpiration() * 1000);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, userName);
        claims.put(Claims.ISSUED_AT, new Date());
        return generateToken(claims);
    }

    protected String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(secretKey)
                .compact();
    }

    public String refreshToken(String token) {
        final Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        claims.put(Claims.ISSUED_AT, new Date());
        return generateToken(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        T7AppUser user = (T7AppUser) userDetails;
        try {
            final String username = getUsernameFromToken(token);
            return ((username != null) &&
                    (username.equals(user.getUsername())
                            && !isTokenExpired(token))
            );
        } catch (JwtException e) {
            // only here exception is handled as this method actually tries to validate a possible invalid token:
            log.warn("Failed to validate token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date tokenExpiration = getExpirationDateFromToken(token);
            return tokenExpiration.before(new Date());
        } catch (ExpiredJwtException e) {
            // Again exception is caught here as this method potentially tries to validate and expired credential.
            return true;
        }
    }

}
