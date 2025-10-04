/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.cucumber.config;

import nl.piter.web.t7.cucumber.util.RestClient;
import nl.piter.web.t7.cucumber.util.jwt.JwtTestTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

/**
 * Configuration Beans for the testing environment.
 * Do not (re)use components from the actual Spring Context but
 * create custom beans for the cucumber test environment here.
 */
@Lazy // because of server port
@Configuration
@Scope(SCOPE_CUCUMBER_GLUE)
@ComponentScan("nl.piter.web.t7.*")
public class WebAppT7TestConfig {

    @LocalServerPort
    private int port;

    @Value("${jwt.secret}")
    private String keySecret;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * This is one stateful RestClient for all tests. It is shared amongst components.
     */
    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE) // because RestClient is stateful
    public RestClient restClient(RestTemplate restTemplate) {
        return new RestClient(restTemplate, "http://localhost:" + port);
    }

    @Bean
    public JwtTestTokenUtil jwtTestTokenUtil() {
        return new JwtTestTokenUtil(keySecret);
    }

}
