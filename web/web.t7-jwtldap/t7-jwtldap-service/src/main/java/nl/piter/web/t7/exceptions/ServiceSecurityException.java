/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.exceptions;

/**
 * Generic Security Exception.
 */
public class ServiceSecurityException extends ServiceException {

    public ServiceSecurityException(String message) {
        super(message);
    }

    public ServiceSecurityException(String message, Throwable e) {
        super(message, e);
    }

}
