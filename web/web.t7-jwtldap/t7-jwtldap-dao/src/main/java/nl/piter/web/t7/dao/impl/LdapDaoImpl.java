/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.impl;

import nl.piter.web.t7.dao.LdapDao;
import nl.piter.web.t7.dao.entities.authority.LdapRole;
import nl.piter.web.t7.dao.repositories.LdapRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class LdapDaoImpl implements LdapDao {

    @Autowired
    private LdapRoleRepository ldapRoles;

    @Override
    public Collection<LdapRole> findByRoleNames(Collection<String> roleNames) {
        return ldapRoles.findByRoleNames(roleNames);
    }

}
