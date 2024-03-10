/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.controller.rest;

import lombok.*;

import java.util.Collection;

/**
 * Authorization information (REST) DTO.
 */
@Data
@ToString
@AllArgsConstructor
public class AuthInfo {

    private String username;
    private Collection<String> roles;

}
