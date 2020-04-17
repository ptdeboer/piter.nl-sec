package nl.piter.web.t6.exceptions;

public class ServiceSecurityException extends ServiceException {

    public ServiceSecurityException(String message) {
        super(message);
    }

    public ServiceSecurityException(String message, Throwable e) {
        super(message, e);
    }

}
