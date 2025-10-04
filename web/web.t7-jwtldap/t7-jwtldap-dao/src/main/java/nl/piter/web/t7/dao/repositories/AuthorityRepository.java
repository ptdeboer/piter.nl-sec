/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.repositories;

import nl.piter.web.t7.dao.entities.authority.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Authority findByAuthorityName(String authorityName);

}
