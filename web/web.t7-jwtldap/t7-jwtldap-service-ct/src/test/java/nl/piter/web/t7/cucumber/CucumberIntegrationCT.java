package nl.piter.web.t7.cucumber;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

// JUnit 5 config:
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class CucumberIntegrationCT {

}
