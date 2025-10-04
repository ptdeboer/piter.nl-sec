/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.config;

import lombok.Getter;
import nl.piter.web.t7.authentication.jwt.JwtAuthenticationTokenFilter;
import nl.piter.web.t7.authentication.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Getter
@Configuration
public class JwtConfig {

    public static final String BEARER_PREFIX = "Bearer";
    public static final String BEARER_PREFIX_WHITESPACE = BEARER_PREFIX + " ";

    final private String tokenHeader;
    final private String secret;
    private final Long expiration;

    @Autowired
    public JwtConfig(@Value("${jwt.secret}") final String secret,
                     @Value("${jwt.expiration}") final Long expiration,
                     @Value("${jwt.header}") final String tokenHeader
    ) {
        this.secret = secret;
        this.expiration = expiration;
        this.tokenHeader = tokenHeader;
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil(JwtConfig jwtConfig) {
        return new JwtTokenUtil(jwtConfig);
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        return new JwtAuthenticationTokenFilter(userDetailsService, jwtTokenUtil);
    }

}
