/* (C) 2020-2022 Piter.NL - free of use, but keep this header. */
//
package nl.piter.web.t6.config;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.secure.CustomerUserDetailsService;
import nl.piter.web.t6.secure.SubjectX509PrincipalExtractor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // No CSRF for non-ui:
        httpSecurity.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // Public:
                .antMatchers("/ping").permitAll()
                // Authenticated:
                .antMatchers("/secure/**").authenticated()
                .and()
                .x509()
                .x509PrincipalExtractor(new SubjectX509PrincipalExtractor())
                .userDetailsService(userDetailsService())
        ;

        // Disable page caching of authenticated headers:
        httpSecurity.headers().cacheControl();
    }

    @Override
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

}
