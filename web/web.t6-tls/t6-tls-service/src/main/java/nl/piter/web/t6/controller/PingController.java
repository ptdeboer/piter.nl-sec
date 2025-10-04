/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.controller.rest.T6Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public ping controller.
 * These url paths must also be allowed in the WebSecurityConfig.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class PingController {

    private final T6Info info;

    @GetMapping(value = "/ping")
    public String ping() {
        log.debug("ping");
        return "pong";
    }

    @GetMapping(value = "/info")
    public T6Info info() {
        log.debug("info");
        return info;
    }
}
