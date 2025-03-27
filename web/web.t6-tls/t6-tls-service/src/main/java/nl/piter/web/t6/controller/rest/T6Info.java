/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Application info RESTS DTO.
 */
@Data
@ToString
@AllArgsConstructor
public class T6Info {

    private final String applicationName;
    private final String version;

}
