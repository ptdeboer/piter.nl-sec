/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.controller;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.controller.rest.T6Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public ping controller.
 * These url paths must also be allowed in the WebSecurityConfig.
 */
@Slf4j
@RestController
public class PingController {

    @Autowired
    T6Info info;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        log.debug("ping");
        return "pong";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public T6Info info() {
        log.debug("info");
        return info;
    }
}
