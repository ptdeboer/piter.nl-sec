/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.controller;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.authentication.service.T7AuthenticationService;
import nl.piter.web.t7.authentication.service.T7UserDetailsService;
import nl.piter.web.t7.authentication.user.T7AppUser;
import nl.piter.web.t7.authentication.user.T7AppUserMapper;
import nl.piter.web.t7.authentication.user.T7LdapUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Query <em>current</em> authenticated User and Roles (Authorities).
 */
@Slf4j
@RestController
public class UserInfoController {

    final private T7AuthenticationService authService;
    final private T7UserDetailsService userDetails;

    @Autowired
    public UserInfoController(T7AuthenticationService authService, T7UserDetailsService userDetails) {
        this.authService = authService;
        this.userDetails = userDetails;
    }

    private UserDetails getContextUserDetails() {

        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails instanceof UserDetails) {
            return (UserDetails) userDetails;
        } else {
            log.debug(" No UserDetails found. Principal Object = <{}>'{}'", userDetails.getClass().getCanonicalName(), userDetails);
        }
        return null;
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public UserDetails getUser() {
        return getContextUserDetails();
    }

    @RequestMapping(value = "/user/roles", method = RequestMethod.GET)
    public List<String> getUserRoles() {

        UserDetails userDetails = getContextUserDetails();
        if (userDetails == null) {
            return new ArrayList<>();
        }

        return this.userDetails.getUserRoles(userDetails.getUsername());
    }

    @RequestMapping(value = "/user/authorities", method = RequestMethod.GET)
    public List<String> getUserAuthorities() {
        UserDetails userDetails = this.getContextUserDetails();
        if (userDetails == null) {
            return new ArrayList<>();
        }
        Collection<? extends GrantedAuthority> auths = userDetails.getAuthorities();
        if (auths == null)
            return new ArrayList<>();
        return userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/ldap", method = RequestMethod.GET)
    public T7LdapUser getUserLdap() {
        UserDetails userDetails = this.getContextUserDetails();
        if (userDetails == null) {
            return null;
        }
        return T7AppUserMapper.toT7LdapUser(this.authService.getLdapPerson(userDetails.getUsername()));
    }

    /**
     * Roles or LDAP memberships for current user.
     */
    @RequestMapping(value = "/user/ldap/roles", method = RequestMethod.GET)
    public List<String> getUserLdapRoles() {
        UserDetails userDetails = this.getContextUserDetails();
        if (userDetails == null) {
            return null;
        }
        return T7AppUserMapper.toT7LdapUser(this.authService.getLdapPerson(userDetails.getUsername())).getMemberShips();
    }

    /**
     * Admin interface. Must have <code>LOCAL_ADMIN</code> authority.
     * Note: Access control on Controller (not service).
     */
    @PreAuthorize("hasAuthority('LOCAL_ADMIN')")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<T7AppUser> getUsers() {

        List<T7AppUser> users = this.userDetails.getUsers();
        return (users != null) ? users : new ArrayList<>();
    }

}
