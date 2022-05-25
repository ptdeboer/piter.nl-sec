/* (C) 2020-2022 Piter.NL - free of use, but keep this header. */
//
package nl.piter.web.t6.exceptions;

/**
 * Unchecked exception.
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
    }

}
