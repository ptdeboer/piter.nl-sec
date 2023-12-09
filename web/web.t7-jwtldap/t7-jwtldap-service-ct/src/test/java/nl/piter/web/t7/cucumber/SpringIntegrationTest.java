package nl.piter.web.t7.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import nl.piter.web.t7.WebAppT7;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

/**
 * Main Integration Start Class which bootstraps the Cucumber tests.
 */
@CucumberContextConfiguration
@ContextConfiguration(classes = WebAppT7.class, loader = SpringBootContextLoader.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@Scope(SCOPE_CUCUMBER_GLUE)
@ActiveProfiles("cctest")
public class SpringIntegrationTest {

}

