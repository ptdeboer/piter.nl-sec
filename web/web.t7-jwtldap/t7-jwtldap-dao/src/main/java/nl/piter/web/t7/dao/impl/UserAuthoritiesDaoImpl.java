/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.impl;


import nl.piter.web.t7.dao.UserAuthoritiesDao;
import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.User;
import nl.piter.web.t7.dao.entities.authority.UserRole;
import nl.piter.web.t7.dao.repositories.AuthorityRepository;
import nl.piter.web.t7.dao.repositories.UserRepository;
import nl.piter.web.t7.dao.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User and Authorities Dao.
 */
@Component
public class UserAuthoritiesDaoImpl implements UserAuthoritiesDao {

    final private UserRepository users;
    final private UserRoleRepository userRoles;
    final private AuthorityRepository authorities;

    @Autowired
    public UserAuthoritiesDaoImpl(UserRepository users, UserRoleRepository userRoles, AuthorityRepository authorities) {
        this.users = users;
        this.userRoles = userRoles;
        this.authorities = authorities;
    }

    public User findByUsername(String username) {
        return users.findByUsername(username);
    }

    @Override
    public Authority findByAuthorityName(String authorityName) {
        return authorities.findByAuthorityName(authorityName);
    }

    @Override
    public User save(User user) {
        return users.save(user);
    }

    // === User Roles === //

    @Override
    public List<UserRole> listUserRoles() {
        return this.userRoles.findAll();
    }

    @Override
    public List<UserRole> listUserRolesForUser(String username) {
        return this.userRoles.listUserRolesForUser(username);
    }

    @Override
    public List<Authority> listAuthoritiesForUserRole(List<String> roleNames) {
        return this.userRoles.listAuthoritiesForUserRole(roleNames);
    }

    @Override
    public List<User> listUsers() {
        return this.users.findAll();
    }

    @Override
    public List<Authority> listAuthorities() {
        return this.authorities.findAll();
    }

    // ==  Authorities == //
    @Override
    public Authority save(Authority authority) {
        return authorities.save(authority);
    }

}
