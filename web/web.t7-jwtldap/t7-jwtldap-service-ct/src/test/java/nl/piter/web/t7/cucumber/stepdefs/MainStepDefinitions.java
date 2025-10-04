/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.cucumber.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.cucumber.SpringIntegrationTest;
import nl.piter.web.t7.cucumber.util.RestClient;
import nl.piter.web.t7.cucumber.util.jwt.JwtTestToken;
import nl.piter.web.t7.cucumber.util.jwt.JwtTestTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MainStepDefinitions extends SpringIntegrationTest {

    // Shared StateFull client //
    @Autowired
    private RestClient restClient;

    @Autowired
    private JwtTestTokenUtil jwtTestTokenUtil;

    private final Map<String, Object> contextProperties = new HashMap<>();

    @When("I perform a simple PING call using url {string}")
    @When("I perform a simple GET call using url {string}")
    public void i_perform_a_simple_ping_call(String url) {
        String pong = this.restClient.doGetString(url);
        log.debug("i_perform_a_simple_ping_call(): response={}", pong);
    }

    @When("I request an JWT Token with user {string} and password {string} at url {string}")
    @Given("a valid JWT Token for user {string} with password {string} using authorization URL {string}")
    public void i_request_and_jwt_token_with_user_and_password_at_url(String user, String pwd, String url) {
        this.restClient.setAuthURl(url);
        this.restClient.jwtAuthenticate(user, pwd);
    }

    @Then("the JWT token must be valid")
    public void the_jwt_token_must_be_valid() {
        restClient.assertNoError();
        JwtTestToken token = this.restClient.getJwtToken();
        log.debug("the_jwt_token_must_be_valid(): token={}", token);
        assertThat(jwtTestTokenUtil.isValid(token.token)).isTrue();
    }

    @Then("the response should be http OK {int}")
    public void the_response_should_be_http_ok(Integer expected) {
        int httpStatus = restClient.getLastHttpStatusCode().value();
        log.debug("the_response_should_be_http_ok(): expecting:{} == {}", expected, httpStatus);
        assertThat(httpStatus).isEqualTo(expected);
    }

    @Then("the response value should be {string}")
    public void the_response_should_be_string(String expected) {
        String responseValue = restClient.getLastResponse().getBody().toString();
        log.debug("the_response_should_be_string(): expecting:'{}' == '{}'", expected, responseValue);
        assertThat(responseValue).isEqualTo(expected);
    }

    @Then("the response should be http ERROR {int}")
    public void the_response_should_be_http_error(Integer httpErrorCode) {
        int statusCode = restClient.getLastErrorStatusCode().value();
        log.debug("the_response_should_be_http_error(): (expected){} == {}", httpErrorCode, statusCode);
        assertThat(statusCode).isEqualTo(httpErrorCode);
    }

    @Then("the JWT token user name must be {string}")
    @Then("the JWT token subject must be {string}")
    public void the_jwt_token_subject_must_be(String name) {
        String tokenName = tokenUtil().getUsernameFromToken(restClient.getJwtToken().token);
        log.debug("the_jwt_token_subject_must_be(): expecting:'{}' == '{}'", name, tokenName);
        assertThat(tokenName).isEqualTo(name);
    }

    @Then("print the JWT token to the INFO log.")
    public void print_the_jwt_token_to_the_info_log() {
        log.info("print_the_jwt_token_to_the_info_log(): token={}", tokenUtil().toString(this.restClient.getJwtToken()));
    }

    private JwtTestTokenUtil tokenUtil() {
        // lazy init:
        return this.jwtTestTokenUtil;
    }

}
