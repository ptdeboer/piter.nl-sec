/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.authentication.jwt;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.authentication.user.T7AppUser;
import nl.piter.web.t7.config.JwtConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class JwtTokenUtilTest {

    private static final String bigSecret = "123456789abcdef01234567890abcdef";
    private static final String bigSecret2 = "X" + bigSecret;

    @BeforeAll // is unit5 keyword
    public static void checkUnit5() {
        log.info("Jupiter/Unit5 enabled");
    }

    @Test
    public void generateTokenIsNotNull() {
        JwtConfig jwtConfig = new JwtConfig(bigSecret, 60L, "Authorization");
        String token = new JwtTokenUtil(jwtConfig).generateToken("jan");
        log.debug("generateToken():token='{}'", token);
        assertThat(token).isNotNull();
        assertThat(token.length()).isGreaterThan(32);
    }

    @Test
    public void getUsernameFromValidTokenReturnsUsername() {
        String userName = "piet";
        // value generated above:
        JwtConfig jwtConfig = new JwtConfig(bigSecret, 60L, "Authorization");
        String token = new JwtTokenUtil(jwtConfig).generateToken(userName);
        String retrievedUserName = new JwtTokenUtil(jwtConfig).getUsernameFromToken(token);

        assertThat(retrievedUserName).isNotNull();
        assertThat(retrievedUserName).isEqualTo(userName);
    }

    @Test
    public void validateValidTokenReturnsTrue() {
        String userName = "piet";
        T7AppUser user = new T7AppUser(1L, userName, null, null, null, false, null, true);

        JwtConfig jwtConfig1 = new JwtConfig(bigSecret, 60L, "Authorization");

        String token = new JwtTokenUtil(jwtConfig1).generateToken(userName);
        boolean valid = new JwtTokenUtil(jwtConfig1).validateToken(token, user);

        assertThat(valid).isTrue();
    }

    @Test
    public void validateInvalidTokenThrowsNoExceptionButReturnsFalse() {
        String userName = "piet";
        T7AppUser user = new T7AppUser(1L, userName, null, null, null, false, null, true);

        JwtConfig jwtConfig1 = new JwtConfig(bigSecret, 60L, "Authorization");
        JwtConfig jwtConfig2 = new JwtConfig(bigSecret2, 60L, "Authorization");

        String token = new JwtTokenUtil(jwtConfig1).generateToken(userName);

        boolean valid = new JwtTokenUtil(jwtConfig2).validateToken(token, user);
        assertThat(valid).isFalse();

    }

    @Test
    public void getUsernameFromInvalidTokenThrowsException() {
        String userName = "piet";
        // value generated above:
        JwtConfig jwtConfig1 = new JwtConfig(bigSecret, 60L, "Authorization");
        JwtConfig jwtConfig2 = new JwtConfig(bigSecret2, 60L, "Authorization");

        String token = new JwtTokenUtil(jwtConfig1).generateToken(userName);
        try {
            String retrievedUserName = new JwtTokenUtil(jwtConfig2).getUsernameFromToken(token);
            assertThat(retrievedUserName).isNull();
        } catch (JwtException e) {
            log.debug("Caught expected exception: <{}>:{}", e.getClass().getCanonicalName(), e.getMessage());
        }
    }

    @Test
    public void isExpiredThrowsNoExceptionButReturnsFalse() throws InterruptedException {
        JwtConfig jwtConfig = new JwtConfig(bigSecret, 0L, "Authorization");
        JwtTokenUtil tokenUtil = new JwtTokenUtil(jwtConfig);
        String token = new JwtTokenUtil(jwtConfig).generateToken("jan");
        log.debug("generateToken():token='{}'", token);
        // nano sleep:
        Thread.sleep(0, 1);
        boolean result = tokenUtil.isTokenExpired(token);
        assertThat(result).isTrue();
    }

}
