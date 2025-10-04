/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.cucumber.stepdefs;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.cucumber.util.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static nl.piter.web.t7.cucumber.util.StringUtil.jsonToStringSet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class UserAuthoritiesStepDefinitions {

    // Shared StateFull client //
    @Autowired
    private RestClient restClient;

    @When("I request the user authorities using a GET to url {string}")
    public void i_request_the_user_authorities_using_a_get_to_url(String authUrl) {
        String response = restClient.doGetString(authUrl);
        log.info("i_request_the_user_authorities_using_a_get_to_url(): response = {}", response);
    }

    @Then("the response must match JSON StringSet:")
    @Then("the response must match JSON StringSet {string}")
    public void the_response_must_match_json_string_set(String strSet) {
        restClient.assertNoError();
        Set<String> expected = jsonToStringSet(strSet);
        log.debug("the_response_must_match_json_string_set(): expected:'{}'", expected);
        Set result = jsonToStringSet(restClient.getLastResponse().getBody().toString());
        assertThat(result).isEqualTo(expected);
    }

}
