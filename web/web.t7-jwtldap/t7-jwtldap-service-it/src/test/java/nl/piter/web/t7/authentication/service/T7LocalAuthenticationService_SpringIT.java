/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.service;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.WebAppT7;
import nl.piter.web.t7.ldap.LdapAccountType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;


/**
 * Test LDAP authentication against embedded ldap server configuration.
 * See: ittest-server.ldif.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("ittest") // use application-ittest.properties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // for data.sql initialization
@SpringBootTest(classes = {WebAppT7.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class T7LocalAuthenticationService_SpringIT {

    @Autowired
    public T7AuthenticationService authService;

    @Test
    public void testAuthenticateAdmin() {
        Authentication authentication = authService.authenticate("admin", "admin", LdapAccountType.UID);
        log.debug("testAuthenticate(): authentication={}", authentication);
        assertTrue("Default test 'admin' user must exist.", authentication.isAuthenticated());
    }


    @Test
    public void testCreateLocalAdmin() {
        UsernamePasswordAuthenticationToken authentication = authService.createLocalAdminAuthentication("admin");
        log.debug("testRequestLocalAdmin(): authentication={}", authentication);
        assertTrue("Local elevation must be possible for user 'admin' user must exist.", authentication.isAuthenticated());
    }

    @Test
    public void testRequestLocalAdmin() {
        authService.requestLocalAdminAuthentication("admin");
        Assert.assertTrue("Local elevation request must be possible", SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
    }


    @Test
    public void testAuthenticateAdminAuthorities() {
        Authentication authentication = authService.authenticate("admin", "admin", LdapAccountType.UID);
        log.debug("testAuthenticate(): authentication={}", authentication);
        assertTrue("Default test 'admin' user must exist.", authentication.isAuthenticated());

        // AuthorityNames:
        List<String> names = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        log.debug("local admin authorities={}", names);
        Assert.assertTrue("Default local admin must have 'ADMIN' role", names.contains("LOCAL_ADMIN"));
    }

}
