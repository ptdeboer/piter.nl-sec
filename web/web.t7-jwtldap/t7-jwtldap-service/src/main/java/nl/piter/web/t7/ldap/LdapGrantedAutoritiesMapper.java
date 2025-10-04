/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Collection;

/**
 * Maps Authorities from LDAP To WebApp Authorities.
 */
@Slf4j
public class LdapGrantedAutoritiesMapper implements GrantedAuthoritiesMapper {

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> ldapAuths) {
        //Todo:
        log.debug("mapAuthorities():Number of granted ldap authorities:{}", ldapAuths.size());
        for (GrantedAuthority auth : ldapAuths) {
            log.debug("mapAuthorities(): - ldap granted authority:{}", auth.getAuthority());
        }
        return ldapAuths;
    }
}
