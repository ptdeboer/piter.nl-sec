/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.rest;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.exceptions.ServiceAccessDeniedException;
import nl.piter.web.t7.exceptions.ServiceAuthenticationException;
import nl.piter.web.t7.exceptions.ServiceSecurityException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Some mapping of Exceptions to HTTP errors. If not they will default to '500' internal server error.
 */
@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        String bodyOfResponse = "Internal Error:" + ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * For generic authentication and authorization failures (unspecified).
     */
    @ExceptionHandler(value = {ServiceSecurityException.class})
    protected ResponseEntity<Object> handleSecurityError(RuntimeException ex, WebRequest request) {
        // WARN and NO stackdump
        log.warn("ServiceSecurityException:{}", ex.getMessage());
        String bodyOfResponse = "Unauthorized access or security credentials are missing.";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * For specific <em>Authentication</em> failures, but named wrongly 'Unauthorized' in HTTP speak.
     */
    @ExceptionHandler(value = {ServiceAuthenticationException.class})
    protected ResponseEntity<Object> handleAuhtenticationError(RuntimeException ex, WebRequest request) {
        // Warn and NO stacktrace dump:
        log.warn("ServiceAuthenticationException: {}", ex.getMessage());
        String bodyOfResponse = "Authentication error or invalid credentials supplied.";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    /**
     * For specific <em>Authorization</em> failures: 403 Forbidden
     */
    @ExceptionHandler(value = {ServiceAccessDeniedException.class})
    protected ResponseEntity<Object> handleAuthorizationError(RuntimeException ex, WebRequest request) {
        // Warn and NO stacktrace dump:
        log.warn("ServiceAccessDeniedException:{}", ex.getMessage());
        String bodyOfResponse = "Unauthorized access or insufficient permissions.";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }
}