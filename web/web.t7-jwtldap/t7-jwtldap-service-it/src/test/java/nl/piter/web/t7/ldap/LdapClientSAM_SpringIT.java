/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.ldap;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.WebAppT7;
import nl.piter.web.t7.config.LdapConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Explicit test SAM account type.
 */
@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("ittest-ldap-sam") // Use application-ittest-ldap-sam.properties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // for data.sql initialization
@SpringBootTest(classes = {WebAppT7.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LdapClientSAM_SpringIT {

    @Autowired
    LdapConfig ldapConfig;

    @Autowired
    LdapTemplate ldapTemplate;

    // custom:
    private LdapClient ldapClient;

    @Before
    public void initLdapClient() {
        // Mixed autowiring and manual configuration to override UID to SAM type.
        ldapConfig.setLdapAccountType(LdapAccountType.SAM);
        this.ldapClient = ldapConfig.ldapClient(ldapTemplate);
    }

    @Test
    public void testLdapListPersons() {
        List<LdapPerson> persons = ldapClient.getAllPersonNames();
        log.debug("testLdapListPersons():persons={}", persons);
        assertNotNull("LDAP test configuration 'ittest-server.ldif' must have at least contain one person.", persons);
        assertTrue("LDAP test configuration 'ittest-server.ldif' must have at least contain one person.", persons.size() > 0);
    }

    @Test
    public void testQueryLdapPerson() {
        LdapPerson ldapPerson = ldapClient.queryUser("piet007");
        log.debug("ldapPerson = {}", ldapPerson);
        assertNotNull("Default test user 'piet007' must exist.", ldapPerson);
    }

    @Test
    public void testLdapPersonMemberships() {
        LdapPerson ldapPerson = ldapClient.queryUser("piet007");
        log.debug("ldapPerson = {}", ldapPerson);
        assertNotNull(ldapPerson);
        assertThat(ldapPerson.getMemberShips().size(), is(1));
        assertThat("Piet must be a member of the 'Kaapvaarders' group.", ldapPerson.getMemberShips().get(0), is("cn=Kaapvaarders,ou=groups,dc=mydomain,dc=com"));
    }

    @Test
    public void testLdapAuthenticateAdmin() {
        boolean authenticated = ldapClient.authenticate("admin", "admin");
        assertTrue("Test user 'admin' must exist and must be authenticated.", authenticated);
    }

    @Test
    public void testLdapAuthenticateUser1a() {
        boolean authenticated = ldapClient.authenticate("jan001", "password");
        assertTrue("Test user authentication failed.", authenticated);
    }

    @Test
    public void testLdapAuthenticateUser2() {
        boolean authenticated = ldapClient.authenticate("piet007", "password");
        assertTrue("Test user authentication failed.", authenticated);
    }

    /**
     * Query persons list in ittest-server.ldif
     */
    @Test
    public void testQueryLdapPersons() {
        List<LdapPerson> persons = ldapClient.getAllPersonNames();
        assertNotNull(persons);
        assertThat(persons.size(), is(4));
        log.debug("ldapPerson[0] = {}", persons.get(0));
    }

}
