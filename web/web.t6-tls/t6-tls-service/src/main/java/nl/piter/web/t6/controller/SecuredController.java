/* (C) 2020-2023 Piter.NL - free of use, but keep this header. */
//
package nl.piter.web.t6.controller;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.service.AuthInfo;
import nl.piter.web.t6.service.T6Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Secured:
 * API is already secured at WebSecurityConfig, but here explicit role checking can also be done.
 */
@Slf4j
@RestController
public class SecuredController {

    @Autowired
    T6Info t6Info;

    // Fine-grained authorization checks can be done by using Roles:
    @PreAuthorize("hasAuthority('CUSTOMER_ROLE')")
    @RequestMapping(value = "/secure", method = RequestMethod.GET)
    public String getSecure() {
        return "Secured!";
    }

    @PreAuthorize("hasAuthority('CUSTOMER_ROLE')")
    @RequestMapping(value = "/secure/auth", method = RequestMethod.GET)
    public AuthInfo getAuthInfo() {

        // Check Security Context and fetch UserDetails as created by UserDetailsService.
        SecurityContext secCtx = SecurityContextHolder.getContext();
        UserDetails details = (UserDetails) secCtx.getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> grantedList = secCtx.getAuthentication().getAuthorities();

        List<String> authList = new ArrayList();
        grantedList.stream().map(Object::toString).forEach(authList::add);
        log.info(" -username :{}", details.getUsername());
        log.info(" -authList :{}", authList);
        return new AuthInfo(details.getUsername(), authList);
    }

}