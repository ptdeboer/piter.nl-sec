/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.repositories;

import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(exported = false)
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT role FROM User user JOIN user.roles role WHERE user.username=?1")
    List<UserRole> listUserRolesForUser(String username);

    @Query("SELECT authority FROM UserRole userRole JOIN userRole.authorities authority WHERE userRole.roleName IN ?1")
    List<Authority> listAuthoritiesForUserRole(List<String> roleName);
}
