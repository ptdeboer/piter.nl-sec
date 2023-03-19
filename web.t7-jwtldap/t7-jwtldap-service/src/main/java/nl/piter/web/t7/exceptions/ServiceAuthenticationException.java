/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.exceptions;

/**
 * Defaults to 401 (Unauthorized) but actually means for <em>Unauthenticated</em>.
 */
public class ServiceAuthenticationException extends ServiceSecurityException {

    public ServiceAuthenticationException(String message) {
        super(message);
    }

    public ServiceAuthenticationException(String message, Throwable e) {
        super(message, e);
    }

}
