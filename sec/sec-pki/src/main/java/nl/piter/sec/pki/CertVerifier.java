/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.sec.pki;

import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

@Slf4j
public class CertVerifier {

    private final X509Certificate x509cert;

    public CertVerifier(X509Certificate cert) {
        this.x509cert = cert;
    }

    /**
     * Verifies whether certificate is signed with a private key that matches Issuer's Public Key.
     */
    public boolean verifyIssuer(PublicKey issuerPK) {
        try {
            x509cert.verify(issuerPK);
            return true;
        } catch (Exception e) {
            log.info("Certificate verification failed for public key: <{}>:{}", e.getClass().getCanonicalName(), e.getMessage());
            return false;
        }
    }

}
