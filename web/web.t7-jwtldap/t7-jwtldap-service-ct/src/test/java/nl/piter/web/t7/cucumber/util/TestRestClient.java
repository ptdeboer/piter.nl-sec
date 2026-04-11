/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.cucumber.util;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.cucumber.exception.CucumberTestException;
import nl.piter.web.t7.cucumber.util.jwt.JwtTestAuthRequest;
import nl.piter.web.t7.cucumber.util.jwt.JwtTestToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Stateful REST Client used for testing.
 * Also handles JWT Token authentication and REST errors.
 */
@Slf4j
public class TestRestClient {

    private final RestClient restClient;
    private final String serviceUrl;

    private String authUrl;
    private JwtTestToken token;

    private RestClient.ResponseSpec lastResponse;
    private Exception lastException;
    private HttpStatusCode lastErrorStatusCode;
    private byte[] lastErrorResponse;

    public TestRestClient(String url) {
        this.restClient = RestClient.builder()
                .baseUrl(url)
                .build();
        this.serviceUrl = url;
        log.debug("<<< New RestClient() >>>"); // Should be created for each scenario as it is stateful.
    }

    public void setAuthURl(String authPath) {
        this.authUrl = authPath;
    }

    public boolean hasError() {
        return (this.lastErrorStatusCode != null) || (this.lastException != null);
    }

    public void assertNoError() {
        if (hasError()) {
            if (this.lastErrorStatusCode != null) {
                throw new CucumberTestException("Cannot get last HTTP status code because of error: " + this.lastErrorStatusCode, this.lastException);
            } else {
                throw new CucumberTestException("Invalid state: lastResponse is null", lastException);
            }
        }
    }

    public String getLastResponseAsString() {
        assertNoError();
        return this.lastResponse.body(String.class);
    }

    public HttpStatusCode getLastHttpStatusCode() {
        assertNoError();
        return this.lastResponse.toBodilessEntity().getStatusCode();
    }

    public HttpStatusCode getLastErrorStatusCode() {
        return this.lastErrorStatusCode;
    }

    public Exception getLastException() {
        return this.lastException;
    }

    public void clear() {
        this.lastResponse = null;
        this.lastException = null;
        this.lastErrorStatusCode = null;
        this.lastErrorResponse = null;
    }

    public HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (token != null) {
            headers.set("Authorization", "Bearer " + token.token);
        }
        return headers;
    }

    public JwtTestToken jwtAuthenticate(String user, String password) {
        this.token = doPostJwtRequest(new JwtTestAuthRequest(user, password));
        return this.token;
    }

    public JwtTestToken getJwtToken() {
        return this.token;
    }

    public JwtTestToken doPostJwtRequest(JwtTestAuthRequest authRequest) {
        return doPost(authUrl, authRequest, null, JwtTestToken.class);
    }

    public String doGetString(String url) {
        return doGet(url, null, String.class);
    }

    public <TR> TR doGet(String url, Class<TR> responseType) {
        return doGet(url, null, responseType);
    }

    public <TR> TR doGet(String url, Map<String, String> queryParams, Class<TR> responseType) {
        log.debug("doGet: {}?{}", url, queryParams);
        clear();

        try {
            URI uri = createFullUri(serviceUrl, url, queryParams);
            // Request:
            HttpHeaders headers = createJsonHeaders();

            log.debug("doGet():uri={}", uri);
            RestClient.ResponseSpec response = restClient.get()
                    .uri(uri)
                    .headers(hdrs -> hdrs.addAll(headers))
                    .retrieve();

            log.debug("GET Response: {} => {}", response, response.body(String.class));
            this.lastResponse = response;
            return response.body(responseType);
        } catch (RestClientException e) {
            // Keep error state, return null
            handle(e, false);
            return null;
        }
    }

    public <TR, TS> TR doPost(String subUrl, TS body, Map<String, String> queryParams, Class<TR> responseType) {
        log.debug("doPost: {}/{}", subUrl, body);
        clear();

        try {
            URI uri = createFullUri(serviceUrl, subUrl, queryParams);
            // Request:
            HttpHeaders headers = createJsonHeaders();
            log.debug("doPost():uri={}", uri);
            // ResponseEntity<TR> response = restTemplate.exchange(uri, HttpMethod.POST, entity, responseType);
            RestClient.ResponseSpec response = restClient.post()
                    .uri(serviceUrl + authUrl)
                    .headers(hdrs -> hdrs.addAll(headers))
                    .body(body)
                    .retrieve();

            log.debug("POST Response: {} => {}", response, response.body(String.class));
            TR responseBody = response.body(responseType);
            this.lastResponse = response;
            return responseBody;
        } catch (RestClientException e) {
            // Keep error state, return null
            handle(e, false);
            return null;
        }
    }

    private URI createFullUri(String serviceUrl, String path, Map<String, String> queryParams) {
        // Query Parameters:
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(serviceUrl + "/" + path);
        if (queryParams != null) {
            queryParams.keySet().forEach(key -> uriBuilder.queryParam(key, queryParams.get(key)));
        }
        return uriBuilder.build()
                .encode()
                .toUri();
    }

    /**
     * Note: Only handle RestClientExceptions!
     * Let others pass.
     */
    protected void handle(RestClientException exception, boolean rethrow) {
        log.error("handle(): RestClientException: {}", exception.getMessage());

        // Update (error) status, and clear previous response.
        this.lastErrorStatusCode = null;
        this.lastErrorResponse = null;
        this.lastResponse = null;
        this.lastException = exception;
        if (exception instanceof HttpStatusCodeException httpException) {
            this.lastErrorStatusCode = httpException.getStatusCode();
            this.lastErrorResponse = httpException.getResponseBodyAsByteArray();
        } else {
            throw exception;
        }

        if (rethrow) {
            throw exception;
        }
    }


}
