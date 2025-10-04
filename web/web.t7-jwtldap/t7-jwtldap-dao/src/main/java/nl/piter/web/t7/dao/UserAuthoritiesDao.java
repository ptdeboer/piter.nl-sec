/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao;

import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.User;
import nl.piter.web.t7.dao.entities.authority.UserRole;

import java.util.List;

/**
 * Combined USER, ROLES and AUTHORITIES dao.
 */
public interface UserAuthoritiesDao {

    // -- Users -- //

    User findByUsername(String username);

    User save(User user);

    List<User> listUsers();

    // -- Roles -- //

    List<UserRole> listUserRoles();

    List<UserRole> listUserRolesForUser(String username);

    // -- Authorities -- //

    Authority findByAuthorityName(String authName);

    Authority save(Authority build);

    List<Authority> listAuthorities();

    List<Authority> listAuthoritiesForUserRole(List<String> roleNames);

}
