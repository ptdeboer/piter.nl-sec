/* (C) 2020 Piter.NL - free of use. */
package nl.piter.web.t6.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable e) {
        super(message, e);
    }

}
