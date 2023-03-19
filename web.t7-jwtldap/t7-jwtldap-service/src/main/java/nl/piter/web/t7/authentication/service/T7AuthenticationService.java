/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.service;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.authentication.user.T7AppUser;
import nl.piter.web.t7.authentication.user.T7AppUserMapper;
import nl.piter.web.t7.dao.LdapDao;
import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.LdapRole;
import nl.piter.web.t7.exceptions.ServiceAuthenticationException;
import nl.piter.web.t7.ldap.LdapAccountType;
import nl.piter.web.t7.ldap.LdapClient;
import nl.piter.web.t7.ldap.LdapPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom AuthenticationService which uses the default (ldap) AuthenticationManager as configured and
 * enhances the authentication context with custom LDAP and/or user attributes stored in the DB.
 * This authentication merges actual Roles and Authorization properties from both LDAP and the DB.
 * This way 'local' roles can be added to the DB complementary to the roles and/or authorities received
 * from the SSO source.
 */
@Slf4j
@Service
public class T7AuthenticationService {

    private final T7UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LdapClient ldapClient;
    private final LdapDao ldapDao;

    @Autowired
    public T7AuthenticationService(T7UserDetailsService userDetailsService,
                                   PasswordEncoder passwordEncoder,
                                   LdapClient ldapClient,
                                   LdapDao ldapDao) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.ldapClient = ldapClient;
        this.ldapDao = ldapDao;
    }

    public LdapPerson getLdapPerson(String username) {
        return ldapClient.queryUser(username);
    }


    public LdapPerson getLdapPerson(String username, LdapAccountType accountType) {
        return ldapClient.queryUser(username, accountType);
    }

    public Authentication authenticate(String username, String password, LdapAccountType accountType) {
        final Authentication authentication;

        // Query local storage
        T7AppUser storedUserDetails = this.userDetailsService.loadUserByUsername(username);
        T7AppUser userDetails;

        // Check for Local user or LDAP user:
        if ((storedUserDetails != null) && (storedUserDetails.isLocalUser())) {
            // Check BCrypted Password:
            if (passwordEncoder.matches(password, storedUserDetails.getPassword())) {
                userDetails = storedUserDetails;
                log.warn("Authenticated local login for user:'{}'", username);
            } else {
                log.warn("Invalid user or wrong password for user: '{}'", username);
                throw new ServiceAuthenticationException("Invalid local user or wrong password:" + username,
                        new BadCredentialsException("Bad credentials for:" + username));
            }
        } else {
//            //  Default Authentication Manager.
//            try {
//            // Note: this uses Spring's LDAP client:
//                authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//            // But here use custom LDAP client:
            if (ldapClient.authenticate(username, password, accountType)) {
                authentication = new UsernamePasswordAuthenticationToken(username, password);
            } else {
                log.warn("Invalid LDAP login for user:'{}'", username);
                throw new ServiceAuthenticationException("LDAP authenticate failed for user:" + username);
            }
            log.info("Authenticated LDAP login for user:'{}'", username);

            // Update/Merge UserDetails as LDAP does not provide all User Details and save updated details in DB:
            T7AppUser updatedUserDetails = updateLdapUserDetails(username, (UserDetails) authentication.getDetails(), accountType);
            userDetails = this.userDetailsService.saveUserDetails(updatedUserDetails);
        }

        // Create Authenticated User with updated Authorities:
        return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
    }

    /**
     * Custom LDAP query to get memberships and add extra roles depending on the membership.
     * This method is called post Authentication so user details can be updated.
     * Note: this uses a custom configured LDAP client.
     */
    protected T7AppUser updateLdapUserDetails(String username, UserDetails userDetails, LdapAccountType accountType) {

        // GrantedAuthorities from (LDAP) AuthenticationManager are currently empty:
        Collection<? extends GrantedAuthority> grantedAuthorities = null;

        if (userDetails != null) {
            grantedAuthorities = userDetails.getAuthorities();
            log.debug(" - num granted authorities:{}", grantedAuthorities.size());
            for (GrantedAuthority auth : grantedAuthorities) {
                log.debug(" - granted authorities:{}" + auth.getAuthority());
            }
        }

        // Merge details with previous stored UserDetails or create new entry:
        T7AppUser storedUserDetails = this.userDetailsService.loadUserByUsername(username);
        if (storedUserDetails == null) {
            storedUserDetails = T7AppUserMapper.createT7User(null, username, username, "", null, false, grantedAuthorities, true);
        }

        // Important: make sure GrantedAuthority implementation is comparable.
        Set<GrantedAuthority> mergedAuthorities = new HashSet<>();
        if (grantedAuthorities != null) {
            grantedAuthorities.stream()
                    .forEach(mergedAuthorities::add);
        }

        // Note: Using custom LDAP Client to get actual LDAP properties. This bypasses Spring.
        LdapPerson ldapPerson = getLdapPerson(username, accountType);
        log.info("LdapPerson = {}", ldapPerson);
        if (ldapPerson == null) {
            // Authentication query mismatch. Check configuration of Spring and custom LdapClient!
            log.error("*** Fixme: Authenticated (ldap) user does not exists or has been removed when performing custom ldap query for:{}", username);
        }

        // Match Ldap Person memberships and map to (Granted) Authorities;
        this.matchStoredLdapAuthorities(ldapPerson).stream()
                .map(auth -> auth.getAuthorityName())
                .forEach(authorityName -> mergedAuthorities.add(new SimpleGrantedAuthority(authorityName)));

        // Merge and map to AppUser:
        return T7AppUserMapper.createT7User(
                storedUserDetails.getId(),
                storedUserDetails.getUsername(),
                (ldapPerson != null) ? ldapPerson.getFullName() : null,
                storedUserDetails.getEmail(),
                storedUserDetails.getPassword(),
                storedUserDetails.isLocalUser(),
                mergedAuthorities,
                storedUserDetails.isEnabled());
    }

    /**
     * Query stored LDAP roles from user store and return authorities for authenticated LdapPerson.
     * Currently, queries LDAP memberships only and converts them to 'roles'.
     */
    protected Collection<Authority> matchStoredLdapAuthorities(LdapPerson ldapPerson) {
        if (ldapPerson == null) {
            return new ArrayList<>();
        }

        // Use Memberships to find matching Roles for now, no Roles have yet been defined.
        List<String> memberShips = ldapPerson.getMemberShips();
        memberShips.stream().forEach(memberShip -> log.debug(" - LDAP memberShip:'{}'", memberShip));
        Collection<LdapRole> ldapRoles = ldapDao.findByRoleNames(memberShips);
        if ((ldapRoles == null) || (ldapRoles.size() == 0)) {
            log.warn("No matching LDAP membership roles for user:{}", ldapPerson.getUserName());
            return new ArrayList<>();
        } else {
            ldapRoles.stream().forEach(role -> {
                log.debug(" - Found matching LDAP Role => Authorities: '{}' => {}",
                        role.getRoleName(),
                        role.getAuthorities().stream()
                                .map(Authority::getAuthorityName)
                                .collect(Collectors.toList()));
            });
        }
        // LDAP Roles to Authorities. Merge authorities from all Roles into flatMap
        return ldapRoles.stream()
                .map(LdapRole::getAuthorities)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Programmatically create a local authorized account.
     */
    protected UsernamePasswordAuthenticationToken createLocalAdminAuthentication(String account) {
        T7AppUser storedUserDetails = this.userDetailsService.loadUserByUsername(account);
        return new UsernamePasswordAuthenticationToken(storedUserDetails.getUsername(), null, storedUserDetails.getAuthorities());
    }

    /**
     * Programmatically request local 'admin' authentication when running in some automated context and there
     * is no call context to access a Secured Service.
     * This method can be used by schedulers or other authenticated services which do not have an SSO id.
     * This method can only be called "from within" this Service.
     */
    public void requestLocalAdminAuthentication(String account) {
        log.info("Requesting local admin privileges for account:{}", account);
        SecurityContextHolder.getContext().setAuthentication(createLocalAdminAuthentication(account));
    }
}
