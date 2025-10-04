/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.cucumber.exception;

/**
 * Unchecked exception for error during Cucumber tests.
 */
public class CucumberTestException extends RuntimeException {
    public CucumberTestException(String message) {
        super(message);
    }

    public CucumberTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
