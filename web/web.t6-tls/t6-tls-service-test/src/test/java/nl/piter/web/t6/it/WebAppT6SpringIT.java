/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.it;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.TestUtil;
import nl.piter.web.t6.WebAppT6;
import nl.piter.web.t6.controller.rest.DomainInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration test with Spring JUnit5 Runner.
 */
@Slf4j
@ExtendWith(SpringExtension.class) // JUnit 5
@ActiveProfiles("ittest") // use application-ittest.properties
@SpringBootTest(classes = {WebAppT6.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebAppT6SpringIT {

    @BeforeAll
    public static void startT6() {
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
    public void restPingReturnsPong() throws Exception {
        String urlPath = "ping";
        RestTemplate restTemplate = TestUtil.createInsecureRestTemplate();
        log.debug("ping:'{}'", urlPath);
        //
        ResponseEntity<String> response = restTemplate.getForEntity(getServiceUrl() + "/" + urlPath, String.class);
        //
        log.debug("RESPONSE: {}", response);
        log.debug("BODY    : {}", response.getBody());
        assertThat(response.getBody()).isEqualTo("pong");
    }

    @Test
    public void restGetToDomainReturnsDomain() throws Exception {
        String urlPath = "domain";
        RestTemplate restTemplate = TestUtil.createSecureRestTemplate("password",
                "classpath:keystores/customer.p12",
                "classpath:keystores/root-ca.ts.p12"); // can use company.p12 here also.
        //
        log.debug("secure:'{}'", urlPath);
        //
        ResponseEntity<DomainInfo> response = restTemplate.getForEntity(getServiceUrl() + "/" + urlPath, DomainInfo.class);
        //
        log.debug("RESPONSE     : {}", response);
        log.debug("BODY         : {}", response.getBody());
        log.debug("DOMAIN INFO: : {}", response.getBody().getInfo());
        assertThat(response.getBody().getInfo()).isNotEmpty();

    }

    @Test
    public void restGetToUnauthorizedDomainReturnsError() throws Exception {
        String urlPath = "domain";
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
