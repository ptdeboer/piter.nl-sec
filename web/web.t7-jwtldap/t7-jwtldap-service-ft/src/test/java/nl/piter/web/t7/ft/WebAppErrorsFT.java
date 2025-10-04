/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.ft;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.WebAppT7;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Check Exceptions and Error Codes.
 * <ul>
 *     <li>5xx: Internal Server error. Something went wrong at the <em>Server</em> side</li>
 *     <li>4xx: Invalid request by <em>client</em>. </li>
 *     <li>401: Named "Unauthorized" but means Unauthenticated/invalid credentials.</li>
 *     <li>403: Forbidden or really Unauthorized/invalid permissons.</li>
 * </ul>
 */
@Slf4j
public class WebAppErrorsFT {

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
        appStarter.startWithProfiles(new String[]{"fttest-errors"}, new String[0]);
        // Update with port used for testing (could be random):
        servicePort = Integer.parseInt(appStarter.getApplicationContext().getEnvironment().getProperty("local.server.port"));
        log.info("servicePort:{}", servicePort);
    }

    @AfterAll
    public static void stopWebApp() {
        appStarter.stop();
    }

    @Test
    public void restPostToAuthReturnsHTTPError401or403() {
        String user = "evilUser";
        String password = "1234";
        String authPath = "/auth";
        int[] errroCodes = new int[]{401, 403};

        WebAppFT.AuthRequest authRequest = new WebAppFT.AuthRequest(user, password);
        log.debug("post:'{}':{}", authPath, authRequest);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<WebAppFT.AuthRequest> request = new HttpEntity<>(authRequest);

        try {
            ResponseEntity<String> response = restTemplate.exchange(getServiceUrl() + "/" + authPath, HttpMethod.POST, request, String.class);
            log.error("Repsponse: {}", response);
            fail("Should have gotten an Exception");
        } catch (HttpClientErrorException e) {
            log.info("Got expected exception: {}", e.getMessage());
            log.info("Got error status code {}", e.getStatusCode());
            // 401 or 403
            assertThat(errroCodes).contains(e.getStatusCode().value());
        }
    }

    // === helpers === //
    private String getServiceUrl() {
        return "http://localhost:" + servicePort;
    }

}
