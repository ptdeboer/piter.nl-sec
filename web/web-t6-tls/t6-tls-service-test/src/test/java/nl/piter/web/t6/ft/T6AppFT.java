/* (C) 2020 Piter.NL - free of use. */
package nl.piter.web.t6.ft;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static junit.framework.TestCase.fail;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional Testing with Application Starter.
 * Clean starter: No integrated SpringRunner context magic.
 */
@Slf4j
public class T6AppFT {

    private static int servicePort;

    private static T6AppStarter appStarter;

    // === //

    @BeforeClass
    public static void startT6() {
        appStarter = new T6AppStarter();
        appStarter.startWithProfiles(new String[]{"fttest"}, new String[0]);
        // Fetch property from actual runtime:
        servicePort = Integer.parseInt(appStarter.getApplicationContext().getEnvironment().getProperty("local.server.port"));
        log.info("servicePort:{}", servicePort);
    }

    @AfterClass
    public static void stopT6() {
        appStarter.stop();
    }

    private String getServiceUrl() {
        return "https://localhost:" + servicePort;
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
