/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.exceptions;

/**
 * Defaults to 403 Forbidden for <em>Unauthorized Access</em>
 */
public class ServiceAccessDeniedException extends ServiceSecurityException {

    public ServiceAccessDeniedException(String message) {
        super(message);
    }

    public ServiceAccessDeniedException(String message, Throwable e) {
        super(message, e);
    }

}
