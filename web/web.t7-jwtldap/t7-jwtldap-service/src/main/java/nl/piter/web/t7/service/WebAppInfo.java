/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.service;

import lombok.*;

@Getter
@NoArgsConstructor
@Setter(AccessLevel.PROTECTED) // Setters are only needed for Jackson.
@ToString
public class WebAppInfo {

    private String applicationName;
    private String buildVersion;
    private String buildTimestamp;

    public WebAppInfo(String applicationName, String buildVersion, String buildTimestamp) {
        this.applicationName = applicationName;
        this.buildVersion = buildVersion;
        this.buildTimestamp = buildTimestamp;
    }

}
