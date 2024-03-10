/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.service;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.controller.rest.DomainInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Example secured Domain Service.
 */
@Service
public class CustomerDomainService {

    // Service Level authority checks, checks for Authorities (no roles here):
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public DomainInfo getDomain() {
        return new DomainInfo("Secured Customer Domain!");
    }

}
