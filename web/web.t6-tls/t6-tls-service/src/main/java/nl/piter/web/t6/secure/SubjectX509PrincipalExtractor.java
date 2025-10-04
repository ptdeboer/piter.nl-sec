/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.secure;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.util.CertUtil;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import java.security.cert.X509Certificate;

@Slf4j
public class SubjectX509PrincipalExtractor implements X509PrincipalExtractor {

    /**
     * Inspect X509 and map to User Principle.
     * Currently, returns "CN = ..."  field.
     *
     * @param clientCert
     * @return CN part of SubjectDN
     */
    @Override
    public String extractPrincipal(X509Certificate clientCert) {
        CertUtil.certUtil().logCertInfo(log, clientCert);
        return CertUtil.certUtil().extractCN(clientCert);
    }

}
