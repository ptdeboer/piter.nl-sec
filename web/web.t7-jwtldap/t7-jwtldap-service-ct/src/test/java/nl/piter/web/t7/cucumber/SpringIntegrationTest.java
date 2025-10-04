/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import nl.piter.web.t7.WebAppT7;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

/**
 * Main Integration Start Class which bootstraps the Cucumber tests.
 */
@CucumberContextConfiguration
@ContextConfiguration(classes = WebAppT7.class, loader = SpringBootContextLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Scope(SCOPE_CUCUMBER_GLUE)
@ActiveProfiles("cctest")
public class SpringIntegrationTest {

}

