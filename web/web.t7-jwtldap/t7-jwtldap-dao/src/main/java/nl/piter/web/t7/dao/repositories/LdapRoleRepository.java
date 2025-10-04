/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.repositories;

import nl.piter.web.t7.dao.entities.authority.LdapRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.List;

@RepositoryRestResource(exported = false)
public interface LdapRoleRepository extends JpaRepository<LdapRole, String> {

    @Query("FROM LdapRole ldapRole WHERE ldapRole.roleName IN ?1")
    List<LdapRole> findByRoleNames(Collection<String> roleNames);

}
