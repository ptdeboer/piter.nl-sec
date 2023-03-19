/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao.repositories;

import nl.piter.web.t7.dao.entities.domain.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShipRepository extends JpaRepository<Ship, Long> {

    Ship findByShipName(String name);

    // Query with optional parameters:
    @Query("SELECT ship FROM Ship ship WHERE (?1=TRUE AND ship.shipName=?2) OR (?3=TRUE AND ship.referenceId=?4)")
    List<Ship> query(boolean queryName, String name, boolean queryRefId, String refId);

}
