/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.authentication.service;

import nl.piter.web.t7.authentication.user.T7AppUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Overrides default 'UserDetail' interface which actual AppUser class.
 */
public interface T7UserDetailsService extends UserDetailsService {

    T7AppUser loadUserByUsername(String username);

    T7AppUser saveUserDetails(T7AppUser userDetails);

    List<String> getUserRoles(String username);

    List<T7AppUser> getUsers();
}
