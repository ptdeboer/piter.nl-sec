/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.cucumber.util;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.cucumber.exception.CucumberTestException;
import nl.piter.web.t7.cucumber.util.jwt.JwtTestAuthRequest;
import nl.piter.web.t7.cucumber.util.jwt.JwtTestToken;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Stateful REST client used for testing.
 * Also handles JWT Token authentication and REST errors.
 */
@Slf4j
public class RestClient {

    private String serviceUrl;

    private final RestTemplate restTemplate;

    private JwtTestToken token;

    private ResponseEntity lastResponse;
    private Exception lastException;
    private HttpStatusCode lastErrorStatusCode;
    private byte[] lastErrorResponse;

    private String authUrl;

    public RestClient(RestTemplate restTemplate, String url) {
        this.restTemplate = restTemplate;
        this.serviceUrl = url;
        log.debug("<<< New RestClient() >>>"); // should be created each scenario.
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

    public ResponseEntity getLastResponse() {
        assertNoError();
        return this.lastResponse;
    }

    public HttpStatusCode getLastHttpStatusCode() {
        assertNoError();
        return this.lastResponse.getStatusCode();
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
        log.debug("doPostJwtRequest:");
        clear();
        try {
            ResponseEntity<JwtTestToken> response = restTemplate.postForEntity(serviceUrl + authUrl, authRequest, JwtTestToken.class);
            log.debug("JWT RESPONSE:{} => {}", response, response.getBody());
            this.lastResponse = response;
            return response.getBody();
        } catch (RestClientException e) {
            // keep error state, return null
            handle(e, false);
            return null;
        }
    }

    public String doGetString(String url) {
        return doGet(url, null, String.class);
    }

    public <TR> TR doGet(String url, Class<TR> responseType) {
        return doGet(url, null, responseType);
    }

    public <TR, TS> TR doGet(String url, Map<String, String> queryParams, Class<TR> responseType) {
        log.debug("doGet: {}?{}", url, queryParams);
        clear();

        // Query Parameters:
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl + "/" + url);
        if (queryParams != null) {
            queryParams.keySet().forEach(key -> builder.queryParam(key, queryParams.get(key)));
        }

        try {
            // Request:
            HttpEntity<TS> entity = new HttpEntity<>(createJsonHeaders());
            URI uri = builder.build().encode().toUri();
            log.debug("doGet():uri={}", uri);
            ResponseEntity<TR> response = restTemplate.exchange(uri, HttpMethod.GET, entity, responseType);

            log.debug("ResponseEntity:{}", response);
            log.debug(" - BODY:{}", response.getBody());
            this.lastResponse = response;
            return response.getBody();
        } catch (RestClientException e) {
            // Keep error state, return null
            handle(e, false);
            return null;
        }
    }


    public <TR, TS> TR doPost(String url, TS body, Map<String, String> queryParams, Class<TR> responseType) {
        log.debug("doPost: {}/{}", url, body);
        clear();

        // Query Parameters:
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUrl + "/" + url);
        if (queryParams != null) {
            queryParams.keySet().forEach(key -> builder.queryParam(key, queryParams.get(key)));
        }

        try {
            // Request:
            HttpEntity<TS> entity = new HttpEntity<>(body, createJsonHeaders());
            URI uri = builder.build().encode().toUri();
            log.debug("doPost():uri={}", uri);
            ResponseEntity<TR> response = restTemplate.exchange(uri, HttpMethod.POST, entity, responseType);

            log.debug("ResponseEntity:{}", response);
            log.debug(" - BODY:{}", response.getBody());
            this.lastResponse = response;
            return response.getBody();
        } catch (RestClientException e) {
            // Keep error state, return null
            handle(e, false);
            return null;
        }
    }

    /**
     * Note: Only handle RestClientExceptions!
     * Let others pass.
     */
    protected void handle(RestClientException exception, boolean rethrow) {
//        log.error("RestClientException: {}", exception.getMessage(),exception);
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

    public void setAuthURl(String authPath) {
        this.authUrl = authPath;
    }
}
