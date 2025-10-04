/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

// test logging levels
@Slf4j
public class TestLogging {

    // Make sure Unit5 is used:
    @BeforeAll
    public static void jupiterUnit5() {
        log.info("BeforeAll(): These tests are Unit5/Jupiter (Jupiter=5th planet) enabled.");
    }

    @Test
    public void testDebug() {
        log.debug("DEBUG");
    }

    @Test
    public void testInfo() {
        log.debug("INFO");
    }

    @Test
    public void testWarn() {
        log.debug("WARN");
    }

    @Test
    public void testError() {
        log.debug("ERROR");
    }

}
