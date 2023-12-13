/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
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
