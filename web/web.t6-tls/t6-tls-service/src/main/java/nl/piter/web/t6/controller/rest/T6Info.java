/* (C) 2020-2023 Piter.NL - free of use, but keep this header. */
//
package nl.piter.web.t6.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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
