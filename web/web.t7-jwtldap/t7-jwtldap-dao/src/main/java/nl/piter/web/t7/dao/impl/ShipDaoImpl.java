/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.impl;


import nl.piter.web.t7.dao.ShipDao;
import nl.piter.web.t7.dao.entities.domain.Ship;
import nl.piter.web.t7.dao.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Ship data.
 */
@Component
public class ShipDaoImpl implements ShipDao {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipDaoImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship find(Long id) {
        Optional<Ship> opt = shipRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public Ship findByName(String shipName) {
        return shipRepository.findByShipName(shipName);
    }

    @Override
    public Ship save(Ship ship) {
        return this.shipRepository.save(ship);
    }

    @Override
    public void delete(Long id) {
        this.shipRepository.deleteById(id);
    }

    @Override
    public List<Ship> query(Optional<String> name, Optional<String> refId) {
        return shipRepository.query(name.isPresent(), name.orElse(""),
                refId.isPresent(), refId.orElse(""));
    }

    @Override
    public List<Ship> listAll() {
        return shipRepository.findAll();
    }

}
