/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.controller;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.dao.entities.domain.Ship;
import nl.piter.web.t7.exceptions.ServiceInvalidParameterException;
import nl.piter.web.t7.service.ShipDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Create/Read/Update/Delete Domain Data.
 * Domain data is directly exposed from Domain DAO.
 */
@Slf4j
@RestController()
@RequestMapping("/data/ships")
public class ShipDataController {

    final private ShipDataService shipService;

    @Autowired
    public ShipDataController(ShipDataService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/{id}")
    public Ship getShip(@PathVariable(value = "id") Long id) {
        return this.shipService.find(id);
    }

    @GetMapping()
    public List<Ship> queryShips(@RequestParam(name = "shipName") Optional<String> optShipName,
                                 @RequestParam(name = "referenceId") Optional<String> optRefId) {
        log.debug("queryShips()");
        return this.shipService.list(optShipName, optRefId);
    }

    /**
     * POST: create new DomainX object.<br>
     * Polymorphic create: either specify new Object as JSON in POST body or supply Query Parameters.
     *
     * @param optShip - new Domain object as JSON body
     * @return
     */
    @PostMapping()
    public Ship createShip(
            @RequestParam(name = "name", required = false) Optional<String> optShipName,
            @RequestParam(name = "description", required = false) Optional<String> optDescription,
            @RequestParam(name = "referenceId", required = false) Optional<String> optRefId,
            @RequestBody(required = false) Optional<Ship> optShip) {
        log.debug("createShip():optShip={}, name={},description={},referenceId={}",
                optShip.isPresent() ? optShip.get() : "<none>", optShipName, optDescription, optRefId);

        Ship newShip;

        if (optShip.isPresent()) {
            // use JSON body.
            newShip = this.shipService.save(optShip.get());
        } else if (optShipName.isEmpty()) {
            throw new ServiceInvalidParameterException("When no JSON body is present, there must at least be one '?xName=...' Query Parameter");
        } else {
            newShip = Ship.builder()
                    .shipName(optShipName.get())
                    .shipDescription(optDescription.orElse(""))
                    .referenceId(optRefId.orElse(null))
                    .build();
        }

        // save and return:
        return this.shipService.save(newShip);
    }

    @PutMapping(value = "/{id}")
    public Ship updateShip(@PathVariable(value = "id") Long id,
                           @RequestBody() Ship ship) {
        log.debug("updateShip():id='{}',ship='{}'", id, ship);
        return this.shipService.update(id, ship);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteShip(@PathVariable(value = "id") Long id) {
        log.debug("deleteShip():id='{}'", id);
        this.shipService.delete(id);
    }

}

