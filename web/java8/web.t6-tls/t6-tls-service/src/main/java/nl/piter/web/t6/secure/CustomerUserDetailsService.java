/* (C) 2020-2023 Piter.NL - free of use, but keep this header. */
//
package nl.piter.web.t6.secure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

import static nl.piter.web.t6.secure.Roles.CUSTOMER_ROLE;

@Slf4j
public class CustomerUserDetailsService implements UserDetailsService {

    private List<String> customers = Arrays.asList("The Customer",
            "Sum Customer",
            "Another Customer");

    /**
     * Map <strong>authenticated</strong> user to <strong>authorized</strong> roles.
     * <p>
     * These UserDetails are returned when inspecting the Authenticated "Principle" object from the SecurityContext.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: {}", username);
        if (customers.contains(username)) {
            log.info("Authorization ok for user: {}", username);
            return new User(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList(CUSTOMER_ROLE.toString()));
        } else {
            // Exception is auto-mapped to 403:
            log.error("Authorization NOT ok. User not authorized:{}", username);
            throw new UsernameNotFoundException("User not authorized:" + username);
        }
    }

}
