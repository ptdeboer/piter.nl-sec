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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Enable Security and configure Json Web Token (JWT) based Authentication Manager.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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


    @Override
    protected void configure(HttpSecurity httpSecurity) {
        try {
            httpSecurity
                    // No CSRF because JWT tokes are invulnerable.
                    .csrf().disable()
                    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                    // Don't create session(s)!
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    // DEMO: Allow X-Frames for h2-console!
                    .headers().frameOptions().disable().and()
                    .authorizeRequests()
                    // DEMO ONLY
                    .antMatchers("/h2-console/**").permitAll()
                    // public info:
                    .antMatchers("/ping").permitAll()
                    .antMatchers("/info/**").permitAll()
                    .antMatchers("/login/**").permitAll()
                    // Make sure to protect used REST apis:
                    .antMatchers("/api/**").authenticated()
                    .antMatchers("/data/**").authenticated()
                    // Allow static webpage, but only at toplevel (non-recursive) !
                    .antMatchers("/*").permitAll()
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated();

            // Custom JWT based authentication filter
            httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

            // Disable page caching
            httpSecurity.headers().cacheControl();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

}
