/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
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
