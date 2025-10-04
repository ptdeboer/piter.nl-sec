/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao;

import nl.piter.web.t7.dao.entities.domain.Ship;

import java.util.List;
import java.util.Optional;

public interface ShipDao {

    Ship find(Long id);

    Ship findByName(String name);

    Ship save(Ship ship);

    void delete(Long id);

    /**
     * Generic Query.
     *
     * @param name  optional name
     * @param refId optional referenceId
     * @return list of matching Ship (or none).
     */
    List<Ship> query(Optional<String> name, Optional<String> refId);

    List<Ship> listAll();

}
