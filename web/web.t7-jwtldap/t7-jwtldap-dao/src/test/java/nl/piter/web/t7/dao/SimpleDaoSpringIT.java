/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao;

import jakarta.transaction.Transactional;
import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Example how to test DAO against in memory DB.
 * Not much to tests to do as this mainly tests the Spring JPA api.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ActiveProfiles("ittest") // use application-ittest.properties
@SpringBootTest(classes = {DaoTestApp.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleDaoSpringIT {

    @Autowired
    public UserAuthoritiesDao userDao;

    @Test
    public void usersRepositoryMustBeEmptyBeforeStart() {
        List users = userDao.listUsers();
        assertEquals("Test database should be empty.", 0, users.size());
    }

    @Test
    public void authoritiesTestDataMustExist() {
        // check test db has proper authorities.
        List<Authority> auths = userDao.listAuthorities();
        List<String> authNames = auths.stream().map(Authority::getAuthorityName).collect(Collectors.toList());
        assertThat(authNames).containsAll(Arrays.asList("ADMIN", "VIEWER", "KVAARDER_ADMIN", "KVAARDER_EDITOR", "KVAARDER_VIEWER"));
    }

    @Test
    public void saveAndRetrieveUser() {
        User user = User.builder()
                .id(null) // no id -> autogenerate(!).
                .username("Dummy")
                .fullname("Crash Test  Dummy")
                .password("<BCRYPTEDSTRING>")
                .build();
        User newUser = userDao.save(user);
        assertThat(newUser.getId()).isGreaterThanOrEqualTo(0);
        assertThat(newUser.getUsername()).isEqualTo("Dummy");
        // etc...
    }

}
