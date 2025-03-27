/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.controller;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.controller.rest.AuthInfo;
import nl.piter.web.t6.controller.rest.DomainInfo;
import nl.piter.web.t6.controller.rest.T6Info;
import nl.piter.web.t6.service.CustomerDomainService;
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
public class DomainController {

    private final T6Info t6Info;
    private final CustomerDomainService domainService;

    @Autowired
    protected DomainController(T6Info t6Info, CustomerDomainService domainService) {
        this.t6Info = t6Info;
        this.domainService = domainService;
    }

    /**
     * Service level authority method. Security check is delegated to DomainService:
     */
    @RequestMapping(value = "/domain", method = RequestMethod.GET)
    public DomainInfo getDomain() {
        return domainService.getDomain();
    }

    /**
     * Controller level authority check.
     */
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @RequestMapping(value = "/domain/myauth", method = RequestMethod.GET)
    public AuthInfo getAuthInfo() {

        // Check Security Context and fetch UserDetails as created by UserDetailsService.
        SecurityContext secCtx = SecurityContextHolder.getContext();
        UserDetails details = (UserDetails) secCtx.getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> grantedList = secCtx.getAuthentication().getAuthorities();

        List<String> authList = new ArrayList();
        grantedList.stream().map(Object::toString).forEach(authList::add);
        log.info("Domain username   : '{}'", details.getUsername());
        log.info("Domain authorities: '{}'", authList);
        return new AuthInfo(details.getUsername(), authList);
    }

}
