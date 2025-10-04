/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
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
