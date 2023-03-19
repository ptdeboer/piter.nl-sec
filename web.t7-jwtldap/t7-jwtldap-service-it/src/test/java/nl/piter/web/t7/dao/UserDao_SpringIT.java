/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.WebApp;
import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Check whether test data from data-ittest.sql is properly populated.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("ittest") // use application-ittest.properties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // for data.sql initialization
@SpringBootTest(classes = {WebApp.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class UserDao_SpringIT {

    @Autowired
    public UserAuthoritiesDao userDao;

    @Test
    public void testDefaultAuthorities() {
        List<Authority> roles = userDao.listAuthorities();
        assertNotNull("No Authorities defined, check data-ittest.sql!", roles);
        roles.forEach(role -> log.debug(" - <Authority>:{}", role));
        assertThat("Number of default Authorities has changed!", roles.size(), is(5));
    }

    @Test
    public void testDefaultUserRoles() {
        List<UserRole> roles = userDao.listUserRoles();
        assertNotNull("No UserRoles defined, check data-ittest.sql!", roles);
        roles.forEach(role -> log.debug(" - <UserRole>:{}", role));
        assertThat("Number of default User Roles has changed!", roles.size(), is(2));
    }

}
