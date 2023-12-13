/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
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
