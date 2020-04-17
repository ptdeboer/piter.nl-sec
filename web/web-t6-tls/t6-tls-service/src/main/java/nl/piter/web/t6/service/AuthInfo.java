/* (C) 2020 Piter.NL - free of use. */
package nl.piter.web.t6.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

/**
 * Simple (immutable) value object.
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class AuthInfo {

    private String username;
    private Collection<String> roles;

}
