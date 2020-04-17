/* (C) 2020 Piter.NL - free of use. */
package nl.piter.web.t6.it;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.T6App;
import nl.piter.web.t6.TestUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test with Spring JUnit Runner.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("ittest") // use application-ittest.properties
@SpringBootTest(classes = {T6App.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class T6AppIT {

    @BeforeClass
    public static void startT6() {
        // check
    }

    // === instance === //

    @LocalServerPort
    int randomServerPort;

    private String getServiceUrl() {
        return "https://localhost:" + getServicePort();
    }

    private int getServicePort() {
        return randomServerPort;
    }

    @Test
    public void testPing() throws Exception {
        String urlPath = "ping";
        RestTemplate restTemplate = TestUtil.createInsecureRestTemplate();
        log.debug("ping:'{}'", urlPath);
        //
        ResponseEntity<String> response = restTemplate.getForEntity(getServiceUrl() + "/" + urlPath, String.class);
        //
        log.debug("RESPONSE: {}", response);
        log.debug("BODY    : {}", response.getBody());
    }

    @Test
    public void testSecure() throws Exception {
        String urlPath = "secure";
        RestTemplate restTemplate = TestUtil.createSecureRestTemplate("password",
                "classpath:keystores/customer.p12",
                "classpath:keystores/root-ca.ts.p12" // can use company.p12 here also
        );

        log.debug("secure:'{}'", urlPath);
        //
        ResponseEntity<String> response = restTemplate.getForEntity(getServiceUrl() + "/" + urlPath, String.class);
        //
        log.debug("RESPONSE: {}", response);
        log.debug("BODY    : {}", response.getBody());
    }

    @Test
    public void testSecureNotAuthorized() throws Exception {
        String urlPath = "secure";
        RestTemplate restTemplate = TestUtil.createInsecureRestTemplate();

        log.debug("secure:'{}'", urlPath);
        try {
            //
            ResponseEntity<String> response = restTemplate.getForEntity(getServiceUrl() + "/" + urlPath, String.class);
            //
            log.debug("RESPONSE: {}", response);
            log.debug("BODY    : {}", response.getBody());
            fail("Unauthorized HTTPS call must result in error.");
        } catch (HttpClientErrorException e) {
            assertThat(e.getRawStatusCode()).isEqualTo(403);
            log.debug("Got expected Http Exception: {}", e.getMessage());
        }
    }

}
