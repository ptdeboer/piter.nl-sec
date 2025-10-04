/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t6.config;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.secure.CustomerUserDetailsService;
import nl.piter.web.t6.secure.SubjectX509PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // Not needed for REST api:
                .csrf(csrf -> csrf.disable())
                // Disable page caching of authenticated headers:
                .headers(headers -> headers.cacheControl(cacheControlConfig -> {}))
                .x509(x509 -> {
                    x509.x509PrincipalExtractor(new SubjectX509PrincipalExtractor());
                    x509.userDetailsService(userDetailsService());
                })
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/ping").permitAll()
                                .requestMatchers("/domain/**").authenticated()
                );

        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

}
