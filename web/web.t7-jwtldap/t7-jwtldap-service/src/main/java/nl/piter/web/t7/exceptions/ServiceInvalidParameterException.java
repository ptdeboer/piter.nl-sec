/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.exceptions;

public class ServiceInvalidParameterException extends ServiceException {

    public ServiceInvalidParameterException(String message) {
        super(message);
    }

    public ServiceInvalidParameterException(String message, Throwable e) {
        super(message, e);
    }
}
