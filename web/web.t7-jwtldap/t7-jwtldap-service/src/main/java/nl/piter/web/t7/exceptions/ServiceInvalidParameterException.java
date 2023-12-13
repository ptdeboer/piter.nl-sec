/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.exceptions;

public class ServiceInvalidParameterException extends ServiceException {

    public ServiceInvalidParameterException(String message) {
        super(message);
    }

    public ServiceInvalidParameterException(String message, Throwable e) {
        super(message, e);
    }
}
