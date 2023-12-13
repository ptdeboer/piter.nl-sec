/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.WebAppT7;
import nl.piter.web.t7.ldap.LdapAccountType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;


/**
 * Spring Context *enabled* Integration testing.
 * Test LDAP authentication against embedded ldap server configuration using SAM accounts.
 * See: ldap-server-ittest.ldif.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional() // lazy initalization of authorities
@ActiveProfiles("ittest-ldap-sam") // use application-ittest-ldap-sam.properties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // for data.sql initialization
@SpringBootTest(classes = {WebAppT7.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class T7LdapAuthenticationServiceSAM_SpringIT {

    /**
     * Spring Context *enabled* testing.
     */
    @Autowired
    public T7AuthenticationService authService;

    @Test
    public void testAuthenticateUser1() {
        Authentication authentication = authService.authenticate("jan001", "password", LdapAccountType.SAM);
        log.debug("testAuthenticate(): authentication={}", authentication);
        assertTrue("Custom user #1 doesn't exists.", authentication.isAuthenticated());
    }

    @Test
    public void testAuthenticateUser2() {
        Authentication authentication = authService.authenticate("piet007", "password", LdapAccountType.SAM);
        log.debug("testAuthenticate(): authentication={}", authentication);
        assertTrue("Custom user #2 doesn't exists.", authentication.isAuthenticated());
    }


    @Test
    @Transactional() // lazy initalization of authorities
    public void testAuthenticateUser3() {
        Authentication authentication = authService.authenticate("joris666", "password", LdapAccountType.SAM);
        log.debug("testAuthenticate(): authentication={}", authentication);
        assertTrue("Custom user #3 doesn't exists.", authentication.isAuthenticated());
    }

    @Test
    @Transactional() // lazy initalization of authorities
    public void testAuthenticatedUser1KaapvaarderAuthorities() {
        String user = "jan001";
        String password = "password";

        Authentication authentication = authService.authenticate(user, password, LdapAccountType.SAM);
        log.debug("testAuthenticate(): authentication={}", authentication);
        assertTrue("Test user:'" + user + "' user must exist.", authentication.isAuthenticated());
        Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
        auths.stream().forEach(auth -> log.debug(" - authority:{}", auth));
        Assert.assertThat(auths.size(), is(2));

        // AuthorityNames:
        List<String> names = auths.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Assert.assertTrue("Didn't find expected AuthorityName. Check data-ittest.sql", names.contains("KVAARDER_ADMIN"));
        Assert.assertTrue("Didn't find expected AuthorityName. Check data-ittest.sql", names.contains("KVAARDER_EDITOR"));
    }

}
