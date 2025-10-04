/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.sec.common.pki;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.bouncycastle.operator.OperatorCreationException;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;

import static nl.piter.sec.common.pki.CertUtil.paddRight;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
public class CertIssuerTest {

    @Test
    void selfSignedRootCA() throws NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException, InvalidNameException, SignatureException, InvalidKeyException, NoSuchProviderException {
        String issuerDN = "CN=nl.piter.rootCA-1,O=Piter.NL,OU=sec,C=FRL";

        CertIssuer.CertificateKeyPair certkeyPair = new CertIssuer()
                .rootCA(issuerDN, true)
                .selfSign();
        logCertificateAttrs(certkeyPair.publicCertificate());
        //
        assertThat(certkeyPair.publicCertificate().getSubjectX500Principal().getName()).isEqualTo(issuerDN);
        assertThat(certkeyPair.publicCertificate().getIssuerX500Principal().getName()).isEqualTo(issuerDN);
    }

    @Test
    void selfSignedRootCAwithCustomSigningKeyPair() throws NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException, InvalidNameException, SignatureException, InvalidKeyException, NoSuchProviderException {
        String issuerDN = "CN=nl.piter.rootCA-2,O=Piter.NL,OU=sec,C=FRL";

        KeyPair kp = new KeyBuilder().size(2048).buildKeyPair();
        CertIssuer.CertificateKeyPair certkeyPair = new CertIssuer()
                .rootCA(issuerDN, false)
                .withSigningKeyPair(kp)
                .selfSign();

        Map<String, Object> attrMap = CertUtil.getAttributesAsMap(certkeyPair.publicCertificate());
        attrMap.entrySet()
                .stream()
                .forEach(entry -> {
                    log.info(" - attr: '{}' = '{}'", entry.getKey(), entry.getValue());
                });
        //
        assertThat(certkeyPair.publicCertificate().getSubjectX500Principal().getName()).isEqualTo(issuerDN);
        assertThat(certkeyPair.publicCertificate().getIssuerX500Principal().getName()).isEqualTo(issuerDN);
        assertThat(certkeyPair.publicCertificate().getPublicKey()).isEqualTo(kp.getPublic());
    }

    @Test
    void signCertificateWithCustomWithSigningKeyPair() throws NoSuchAlgorithmException, CertificateException, OperatorCreationException, InvalidNameException, SignatureException, NoSuchProviderException, InvalidKeyException, IOException {
        String issuerDN = "CN=nl.piter.rootCA-2,O=Piter.NL,OU=sec,C=FRL";
        String subjectDN = "CN=customer-1,O=Sumcorp,OU=test,C=FRL";

        KeyPair kp = new KeyBuilder().size(2048).buildKeyPair();
        X509Certificate cert = new CertIssuer()
                .withSigningKeyPair(kp)
                .withIssuerDN(issuerDN)
                .signCertificate(subjectDN,
                        kp.getPublic(),
                        BigInteger.valueOf(System.currentTimeMillis()),
                        new Date(System.currentTimeMillis())
                        , 10
                );

        logCertificateAttrs(cert);
        //
        assertThat(CertUtil.parseRDN(cert.getSubjectX500Principal().getName(), true)).isEqualTo(CertUtil.parseRDN(subjectDN, true));
        assertThat(CertUtil.parseRDN(cert.getIssuerX500Principal().getName(), true)).isEqualTo(CertUtil.parseRDN(issuerDN, true));
        AssertionsForClassTypes.assertThat(cert.getPublicKey()).isEqualTo(kp.getPublic());
    }

    @Test
    void signCertificateWithCustomRootCA() throws NoSuchAlgorithmException, CertificateException, OperatorCreationException, InvalidNameException, SignatureException, NoSuchProviderException, InvalidKeyException, IOException {
        String issuerDN = "CN=nl.piter.rootCA-3,O=Piter.NL,OU=sec,C=FRL";
        String subjectDN = "CN=customer-2,O=Sumcorp,OU=test,C=FRL";

        CertIssuer.CertificateKeyPair rootCA = new CertIssuer()
                .rootCA(issuerDN, true)
                .selfSign();

        KeyPair myKeyPair = new KeyBuilder().size(2048).buildKeyPair();
        X509Certificate cert = new CertIssuer()
                .withIssuerCertAndKeyPair(rootCA.publicCertificate(), rootCA.keyPair())
                .signCertificate(subjectDN, myKeyPair.getPublic());

        logCertificateAttrs(cert);
        //
        assertThat(CertUtil.parseRDN(cert.getSubjectX500Principal().getName(), true)).isEqualTo(CertUtil.parseRDN(subjectDN, true));
        assertThat(CertUtil.parseRDN(cert.getIssuerX500Principal().getName(), true)).isEqualTo(CertUtil.parseRDN(issuerDN, true));
    }

    @Test
// Example for JavaDoc:
    void docExample() throws InvalidNameException, NoSuchAlgorithmException, CertificateException, IOException, SignatureException, OperatorCreationException, InvalidKeyException, NoSuchProviderException {
        CertIssuer.CertificateKeyPair certkeyPair = new CertIssuer()
                .rootCA("CN=my-self-signed-cert,O=test-env", true)
                .selfSign();
        X509Certificate cert = certkeyPair.publicCertificate();
        KeyPair keyPair = certkeyPair.keyPair();
        //
        assertThat(cert).isNotNull();
        assertThat(keyPair).isNotNull();
    }

    //@Test
    void writeSelfSignedRootCA() throws NoSuchAlgorithmException, CertificateException, IOException, OperatorCreationException, InvalidNameException, SignatureException, InvalidKeyException, NoSuchProviderException {
        String issuerDN = "CN=nl.piter.rootCA-5,O=Piter.NL,OU=sec,C=FRL";
        CertIssuer.CertificateKeyPair certkeyPair = new CertIssuer()
                .rootCA(issuerDN, true)
                .selfSign();

        CertUtil.saveCert(Paths.get("/tmp/rootca.crt"), certkeyPair.publicCertificate());
    }

    private static void logCertificateAttrs(X509Certificate cert) throws CertificateParsingException {
        Map<String, Object> attrMap = CertUtil.getAttributesAsMap(cert);
        attrMap.entrySet()
                .stream()
                .forEach(entry ->
                        log.info(" - attr: {} = '{}'", paddRight("'" + entry.getKey() + "'", 18), entry.getValue()));
    }

}
