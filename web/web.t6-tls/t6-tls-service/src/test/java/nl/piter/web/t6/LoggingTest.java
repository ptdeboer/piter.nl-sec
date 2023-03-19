package nl.piter.web.t6;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * For actual integration tests, see -service-test module.
 */
@Slf4j
public class LoggingTest {

    @Test
    public void logInfo() {
        log.info( "Info logging enabled.");
    }

}
