/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.config;

import nl.piter.web.t7.authentication.jwt.JwtAuthenticationEntryPoint;
import nl.piter.web.t7.authentication.jwt.JwtAuthenticationTokenFilter;
import nl.piter.web.t7.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Enable Security and configure Json Web Token (JWT) based Authentication Manager.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    // ---
    // DISABLED
    // We're using custom LDAP client: See: T7AuthenticationService
//    @Autowired
//    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) {
//
//        try {
////            authenticationManagerBuilder
////                    .userDetailsService(this.userDetailsService)
////                    .passwordEncoder(passwordEncoder());
//            authenticationManagerBuilder.ldapAuthentication()
//                    .userSearchBase(ldapConfig.getUserSearchBase())
//                    .userSearchFilter(ldapConfig.getUserSearchFilter())
//                    .authoritiesMapper(new LdapGrantedAutoritiesMapper())
//                    .contextSource(contextSource);
//        } catch (Exception e) {
//            throw new ServiceException(e.getMessage(), e);
//        }
//    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        try {
            httpSecurity
                    // No CSRF because (non cookie) JWT tokes are invulnerable.
                    .csrf((csrf) -> csrf.disable())
                    .exceptionHandling((handling) -> handling.authenticationEntryPoint(unauthorizedHandler))
                    // Default to stateless (no sessions).
                    .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    // DEMO: To allow X-Frames for h2-console: disable frame options.
                    .headers((headers) -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
                    .authorizeHttpRequests((authorize) -> authorize
                            // public info:
                            .requestMatchers(antMatcher("/ping")).permitAll()
                            .requestMatchers(antMatcher("/info/**")).permitAll()
                            .requestMatchers(antMatcher("/login/**")).permitAll()
                            // Make sure to protect used REST apis:
                            // DEMO: h2 interface, but restrict access:
                            .requestMatchers(antMatcher("/h2-console/**")).authenticated()
                            .requestMatchers(antMatcher("/api/**")).authenticated()
                            .requestMatchers(antMatcher("/data/**")).authenticated()
                            // Allow static webpage, but only at top-level (non-recursive) !
                            .requestMatchers(antMatcher("/*")).permitAll()
                            .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()
                            .anyRequest().authenticated()
                    );

            // Custom JWT based authentication filter
            httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

            // Disable page caching, slightly slower, but more secure:
            httpSecurity.headers((headers) -> headers.cacheControl((cache) -> cache.disable()));

            return httpSecurity.build();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
