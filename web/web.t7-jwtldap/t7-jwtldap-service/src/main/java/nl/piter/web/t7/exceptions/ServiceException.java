/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.exceptions;

/**
 * Unchecked Parent Exception for all Security Exceptions.
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable e) {
        super(e);
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
    }

}
