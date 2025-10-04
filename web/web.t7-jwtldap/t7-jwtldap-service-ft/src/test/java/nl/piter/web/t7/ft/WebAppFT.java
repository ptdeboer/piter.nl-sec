/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.ft;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.WebAppT7;
import nl.piter.web.t7.service.WebAppInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests against started WebApp.
 * Clean starter: No integrated autowired SpringContextConfigurationRunner blah...
 */
@Slf4j
public class WebAppFT {

    private static int servicePort;

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

    private static WebAppStarter appStarter;

    // === //

    @BeforeAll // Jupiter Unit5 (!)
    public static void startWebApp() {
        appStarter = new WebAppStarter(WebAppT7.class);
        appStarter.startWithProfiles(new String[]{"fttest"}, new String[0]);
        // Update with port used for testing (could be random):
        servicePort = Integer.parseInt(appStarter.getApplicationContext().getEnvironment().getProperty("local.server.port"));
        log.info("servicePort:{}", servicePort);
    }

    @AfterAll
    public static void stopWebApp() {
        appStarter.stop();
    }

    private String getServiceUrl() {
        return "http://localhost:" + servicePort;
    }

    /**
     * curl --request POST \
     * --url http://localhost:9001/auth \
     * --header 'content-type: application/json' \
     * --data '{"username": "jan", "password": "password"}'
     */
    @Test
    public void restPostToAuthReturnsToken() {
        String user = "jan";
        String password = "password";
        String authPath = "auth";

        JwtToken token = getTokenFor(getServiceUrl() + "/" + authPath, user, password);
        assertThat(token).isNotNull();
    }

    /**
     * Test unauthorized access:
     */
    @Test
    public void restPostToPingReturnsPong() {
        String urlPath = "ping";
        // UN-authenticated call:
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(null));
        ResponseEntity<String> rolesResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );
        // Response:
        String pong = rolesResponse.getBody();
        log.debug("Info: {}", pong);
        assertThat(pong).isEqualTo("pong");
    }

    @Test
    public void restGetFromInfoReturnsInfoDTO() {
        String urlPath = "info";

        // UN-authenticated call:
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(null));
        ResponseEntity<WebAppInfo> rolesResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );
        // Response:
        WebAppInfo info = rolesResponse.getBody();
        log.debug("Info: {}", info);
    }

    @Test
    public void restGetFromUserAuthoritiesReturnsUserAuthorities() {
        String user = "jan";
        String password = "password";
        String authPath = "auth";
        String urlPath = "user/authorities";
        String[] expectedAuths = new String[]{"KAAPVAARDERS_ADMIN", "KAAPVAARDERS_VIEWER", "KAAPVAARDERS_EDITOR"};

        JwtToken token = getTokenFor(getServiceUrl() + "/" + authPath, user, password);

        // Authenticated call:
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(token.token));
        ResponseEntity<List<String>> rolesResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );
        // Response:
        List<String> autorities = rolesResponse.getBody();
        log.debug("User Granted Authorities: {}", autorities);
        assertThat(autorities).contains(expectedAuths[0]);
        assertThat(autorities).contains(expectedAuths[1]);
    }

    @Test
    public void restGetFromUserLdapRolesReturnsRoles() {
        String user = "jan";
        String password = "password";
        String authPath = "auth";
        String urlPath = "user/ldap/roles";
        String[] expectedRoles = new String[]{//
                "cn=g-domain-kaapvaarders-admin,ou=groups,dc=kaapvaarders,dc=com",
                "cn=g-domain-kaapvaarders-viewers,ou=groups,dc=kaapvaarders,dc=com"
        };

        JwtToken token = getTokenFor(getServiceUrl() + "/" + authPath, user, password);

        // Authenticated call:
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(token.token));
        ResponseEntity<List<String>> rolesResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        // Response:
        List<String> roles = rolesResponse.getBody();
        log.debug("LDAP User Roles (aka 'memberships')= {}", roles);
        assertThat(roles).contains(expectedRoles[0]);
        assertThat(roles).contains(expectedRoles[1]);
    }

    @Test
    public void restGetFromUserAuthoritiesReturnsAuthorities() {
        String user = "admin";
        String password = "admin";
        String authPath = "auth";
        String urlPath = "user/authorities";
        String[] expectedAuths = new String[]{"LOCAL_ADMIN", "LOCAL_EDITOR"};

        JwtToken token = getTokenFor(getServiceUrl() + "/" + authPath, user, password);

        // Authenticated call:
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, createJsonHeaders(token.token));
        ResponseEntity<List<String>> rolesResponse = restTemplate.exchange(getServiceUrl() + "/" + urlPath,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });
        // Response:
        List<String> auths = rolesResponse.getBody();
        log.debug("Local User (admin) Authorities={}", auths);
        assertThat(auths).contains(expectedAuths[0]);
        assertThat(auths).contains(expectedAuths[1]);
    }

    // --- Test Helpers --- //

    protected static HttpHeaders createJsonHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        return headers;
    }

    protected static JwtToken getTokenFor(String urlPath, String user, String password) {
        AuthRequest authRequest = new AuthRequest(user, password);
        log.debug("post:'{}':{}", urlPath, authRequest);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JwtToken> tokenRepsponse = restTemplate.postForEntity(urlPath, authRequest, JwtToken.class);
        JwtToken token = tokenRepsponse.getBody();
        log.debug("Token: {}", token);
        assertThat(token).isNotNull();
        assertThat(token.token).isNotEqualTo("");
        return token;
    }

}
