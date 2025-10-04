/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.service;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.dao.ShipDao;
import nl.piter.web.t7.dao.entities.domain.Ship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Data Service returns actual (domain) Entities.
 * No Domain <-> DAP mapping is done here.
 */
@Slf4j
@Service
public class ShipDataService {

    final private ShipDao shipDao;

    @Autowired
    public ShipDataService(ShipDao shipDao) {
        this.shipDao = shipDao;
    }

    @PreAuthorize("hasAuthority('KAAPVAARDERS_VIEWER')")
    public List<Ship> list(Optional<String> name, Optional<String> refId) {
        if (name.isPresent() && refId.isEmpty()) {
            return this.shipDao.listAll();
        } else {
            return this.shipDao.query(name, refId);
        }
    }

    @PreAuthorize("hasAuthority('KAAPVAARDERS_VIEWER')")
    public Ship find(Long id) {
        return this.shipDao.find(id);
    }

    @PreAuthorize("hasAuthority('KAAPVAARDERS_VIEWER')")
    public Ship findByName(String name) {
        return this.shipDao.findByName(name);
    }

    /**
     * Create or Update.
     */
    @PreAuthorize("hasAuthority('KAAPVAARDERS_EDITOR')")
    public Ship save(Ship domainX) {
        return this.shipDao.save(domainX);
    }

    @PreAuthorize("hasAuthority('KAAPVAARDERS_ADMIN')")
    public void delete(Long id) {
        this.shipDao.delete(id);
    }

    @PreAuthorize("hasAuthority('KAAPVAARDERS_EDITOR')")
    public Ship update(Long id, Ship ship) {
        Ship orgShip = this.shipDao.find(id);
        if (orgShip == null) {
            throw new ResourceNotFoundException("Ship doesn't exist for id=" + id);
        }
        // MERGE optional attributes into actual connected Entity. Skip if new attribute is null:
        if (ship.getShipName() != null) {
            orgShip.setShipName(ship.getShipName());
        }
        if (ship.getShipDescription() != null) {
            orgShip.setShipDescription(ship.getShipDescription());
        }
        if (ship.getReferenceId() != null) {
            orgShip.setReferenceId(ship.getReferenceId());
        }
        log.info("Updating Ship Entity: {}", orgShip);
        return this.shipDao.save(orgShip);
    }
}
