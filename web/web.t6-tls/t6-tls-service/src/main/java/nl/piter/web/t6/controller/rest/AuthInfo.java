/* (C) 2020-2023 Piter.NL - free of use, but keep this header. */
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
