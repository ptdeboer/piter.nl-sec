package nl.piter.web.t7.cucumber.exception;

/**
 * Unchecked exception for error during Cucumber tests.
 */
public class CucumberTestException extends  RuntimeException {
    public CucumberTestException(String message) {
        super(message);
    }

    public CucumberTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
