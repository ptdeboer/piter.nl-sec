/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.controller;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.service.WebAppInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class InfoController {

    final private WebAppInfo t7Info;

    @Autowired
    public InfoController(WebAppInfo t7Info) {
        this.t7Info = t7Info;
    }

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        log.debug("ping");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        log.info("Authenticated user:{}", currentPrincipalName);
        authentication.getCredentials();
        log.info("Authenticated authorities:{}", authentication.getAuthorities());
        return "pong";
    }

    @RequestMapping(value = "/info/version", method = RequestMethod.GET)
    public String version() {
        return t7Info.getBuildVersion();
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public WebAppInfo getInfo() {
        return t7Info;
    }

}
