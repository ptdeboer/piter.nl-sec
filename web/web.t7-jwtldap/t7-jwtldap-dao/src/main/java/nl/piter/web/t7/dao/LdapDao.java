/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao;

import nl.piter.web.t7.dao.entities.authority.LdapRole;

import java.util.Collection;

public interface LdapDao {

    Collection<LdapRole> findByRoleNames(Collection<String> roleNames);

}
