/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao;

import nl.piter.web.t7.dao.entities.authority.LdapRole;

import java.util.Collection;

public interface LdapDao {

    Collection<LdapRole> findByRoleNames(Collection<String> roleNames);

}
