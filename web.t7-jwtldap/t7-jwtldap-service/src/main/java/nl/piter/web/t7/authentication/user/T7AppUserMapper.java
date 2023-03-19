/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.user;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.User;
import nl.piter.web.t7.ldap.LdapPerson;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper which maps Users and Authorities from DAO to AppUsers and UserDetail objects.
 */
@Slf4j
public final class T7AppUserMapper {

    /**
     * Create AppUser from User Entity (DAO).
     *
     * @param user user entity from Authorization Interface.
     * @return AppUser
     */
    public static T7AppUser toT7User(User user) {
        return new T7AppUser(
                user.getId(),
                user.getUsername(),
                user.getFullname(),
                user.getEmail(),
                user.getPassword(),
                !Strings.isNullOrEmpty(user.getPassword()),
                mapToGrantedAuthorities(user.getCachedAuthorities()),
                user.getEnabled()
        );
    }

    /**
     * Create AppUser from User properties
     */
    public static T7AppUser createT7User(Long id,
                                         String username,
                                         String fullname,
                                         String email,
                                         String password,
                                         boolean localUser,
                                         List<String> authorities,
                                         Boolean enabled) {
        //
        return new T7AppUser(id,
                username,
                fullname,
                email,
                password,
                localUser,
                stringMapToGrantedAuthorities(authorities),
                enabled);
    }

    /**
     * Create T5User from User properties
     */
    public static T7AppUser createT7User(Long id,
                                         String username,
                                         String fullname,
                                         String email,
                                         String password,
                                         boolean localUser,
                                         Collection<? extends GrantedAuthority> grantedAuthorities,
                                         Boolean enabled) {
        //
        return new T7AppUser(id,
                username,
                fullname,
                email,
                password,
                localUser,
                grantedAuthorities,
                enabled);
    }

    public static List<GrantedAuthority> mapToGrantedAuthorities(List<Authority> authorities) {
        if (authorities == null) {
            return new ArrayList();
        }
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
    }

    /**
     * Filter authorities if defined in AuthorityName enumeration and return as List of GrantedAuthorities.
     */
    public static List<GrantedAuthority> stringMapToGrantedAuthorities(List<String> authorities) {
        if (authorities == null) {
            return new ArrayList();
        }
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public static List<String> toStringList(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            return new ArrayList();
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    public static T7LdapUser toT7LdapUser(LdapPerson ldapPerson) {
        if (ldapPerson == null) {
            return null;
        }
        return T7LdapUser.builder()
                .userName(ldapPerson.getUserName())
                .fullName(ldapPerson.getFullName())
                .principalName(ldapPerson.getPrincipalName())
                .memberShips(ldapPerson.getMemberShips())
                .build();
    }

}
