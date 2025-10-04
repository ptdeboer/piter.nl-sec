/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
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
        log.info("Junit5/Jupiter enabled.");
    }

}
