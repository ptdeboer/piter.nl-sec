/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7;

import jakarta.transaction.Transactional;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.authentication.user.T7LdapUser;
import nl.piter.web.t7.dao.UserAuthoritiesDao;
import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("ittest") // use application-ittest.properties
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // for data.sql initialization
@SpringBootTest(classes = {WebAppT7.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class T7WebAppSpringIT {


    @ToString
    public static class AuthRequest {
        public String username;
        public String password;

        public AuthRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @ToString
    public static class JwtToken {
        public String token;
    }

    // === //

    private static HttpHeaders createJsonHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        return headers;
    }

    // === instance === //

    @LocalServerPort
    int randomServerPort;

    @Autowired
    UserAuthoritiesDao userDao;

    private String getServiceUrl() {
        return "http://localhost:" + getServicePort();
    }

    private int getServicePort() {
        return randomServerPort;
    }

    /**
     * curl --request POST \
     * --url http://localhost:9001/auth \
     * --header 'content-type: application/json' \
     * --data '{"username": "jan", "password": "password"}'
     */
    @Test
    public void testPostAuth() {
        String user = "jan";
        String password = "password";
        String urlPath = "auth";

        AuthRequest authRequest = new AuthRequest(user, password);
        RestTemplate restTemplate = new RestTemplate();
        log.debug("post:'{}':{}", urlPath, authRequest);
        ResponseEntity<JwtToken> response = restTemplate.postForEntity(getServiceUrl() + "/" + urlPath, authRequest, JwtToken.class);

        //assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        log.debug("JWT RESPONSE:{} => {}", response, response.getBody());
        JwtToken token = response.getBody();
        log.debug("Token = {}", token);
        assertThat(token.token).isNotNull();

    }

    @Test
    public void testGetUserAuthorities() {
        String user = "jan";
        String password = "password";
        String authPath = "auth";
        String urlPath = "user/authorities";
        String[] expectedRoles = new String[]{"KVAARDER_ADMIN", "KVAARDER_EDITOR"};

        AuthRequest authRequest = new AuthRequest(user, password);
        RestTemplate restTemplate = new RestTemplate();
        log.debug("post:'{}':{}", urlPath, authRequest);

        // Create token:
        ResponseEntity<JwtToken> tokenRepsonse = restTemplate.postForEntity(getServiceUrl() + "/" + authPath, authRequest, JwtToken.class);
        JwtToken token = tokenRepsonse.getBody();
        log.debug("Token = {}", token);
        assertThat(token.token).isNotEmpty();

        // Authenticated call:
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(token.token));
        ResponseEntity<List<String>> authsResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<String>>() {
                });
        // Response:
        List<String> authorities = authsResponse.getBody();
        log.debug("Roles={}", authorities);
        assertThat(authorities).contains(expectedRoles[0]);
        assertThat(authorities).contains(expectedRoles[1]);

        // Check DAO:
        User details = userDao.findByUsername(user);
        List<Authority> cachedAuths = details.getCachedAuthorities();
        cachedAuths.stream()
                .forEach(auth -> log.error(" - <DAO> cached authority:{}", auth));

    }

    @Test
    public void testGetLdapUser() {
        String user = "jan";
        String password = "password";
        String authPath = "auth";
        String urlPath = "user/ldap";

        AuthRequest authRequest = new AuthRequest(user, password);
        RestTemplate restTemplate = new RestTemplate();
        log.debug("post:'{}':{}", urlPath, authRequest);

        // Create token:
        ResponseEntity<JwtToken> tokenRepsonse = restTemplate.postForEntity(getServiceUrl() + "/" + authPath, authRequest, JwtToken.class);
        JwtToken token = tokenRepsonse.getBody();
        log.debug("Token = {}", token);
        assertThat(token.token).isNotEmpty();

        // Authenticated call:
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(token.token));
        ResponseEntity<T7LdapUser> ldapResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity, T7LdapUser.class);

        T7LdapUser ldapUser = ldapResponse.getBody();
        log.debug("LdapPerson:{}", ldapUser);

        assertThat(ldapUser).isNotNull();
        assertThat(ldapUser.getMemberShips().size()).isEqualTo(1);
        assertThat(ldapUser.getMemberShips()).contains("cn=Kaapvaarders,ou=groups,dc=mydomain,dc=com");
    }

    @Test
    public void testGetUserRoles() {
        String user = "jan";
        String password = "password";
        String authPath = "auth";
        String urlPath = "user/roles";

        AuthRequest authRequest = new AuthRequest(user, password);
        RestTemplate restTemplate = new RestTemplate();
        log.debug("post:'{}':{}", urlPath, authRequest);

        // Create token:
        ResponseEntity<JwtToken> tokenRepsonse = restTemplate.postForEntity(getServiceUrl() + "/" + authPath, authRequest, JwtToken.class);
        JwtToken token = tokenRepsonse.getBody();
        log.debug("Token = {}", token);
        assertThat(token.token).isNotEmpty();

        // Authenticated call:
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(token.token));
        ResponseEntity<List<String>> authsResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<String>>() {
                });

        // Response:
        List<String> roles = authsResponse.getBody();
        // No Roles yet testing REST call only:
        log.debug("Roles={}", roles);
        assertThat(roles.size()).isGreaterThanOrEqualTo(0);
    }

}
