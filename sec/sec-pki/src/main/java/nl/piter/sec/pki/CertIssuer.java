/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.sec.pki;

import lombok.Getter;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import javax.naming.InvalidNameException;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import static nl.piter.sec.pki.BCInitializer.BOUNCY_CASTLE_PROVIDER_NAME;

/**
 * Minimalistic Certificate Issuer for testing environments.<br>
 * To create a minimal self-signed (root) Certificate  with Public/Private Keypair, do for example:
 * <pre>
 * CertIssuer.CertificateKeyPair certkeyPair = new CertIssuer()
 *    .rootCA("CN=my-self-signed-cert,O=test-env", true)
 *    .selfSign();
 * X509Certificate cert=certkeyPair.publicCertificate();
 * KeyPair keyPair = certkeyPair.keyPair();
 * </pre>
 */
@Getter
public class CertIssuer {

    // Issuer
    private KeyPair signingKeypair;
    private int keySize = 2048;
    private String issuerDN;
    private String signatureAlgorithm = "SHA256WithRSA";
    // Certificate
    private boolean isRoot = false;
    private String subjectDN;
    private Date notBeforeDate;
    private Date notAfterDate;
    private int lifetimeInYears = 10;
    private BigInteger certSerialNumber;
    //
    private final Provider provider = BCInitializer.getProvider(BOUNCY_CASTLE_PROVIDER_NAME);

    /**
     * Fill in all minimal required attributes for a self-signed (Root CA) Certificate.
     */
    public CertIssuer rootCA(String subjectDN, boolean autoGenerateKeyPair) throws NoSuchAlgorithmException, InvalidNameException {
        long now = System.currentTimeMillis();
        this.withSubjectDN(subjectDN);
        this.withIssuerDN(subjectDN);
        this.notBeforeDate = new Date(now);
        this.isRoot = true;
        this.lifetimeInYears = 10;
        this.certSerialNumber = new BigInteger(Long.toString(now));
        if (autoGenerateKeyPair) {
            this.generateKeyPair();
        }
        return this;
    }

    public CertIssuer withSigningKeyPair(KeyPair keyPair) {
        this.signingKeypair = keyPair;
        return this;
    }

    public CertIssuer withSigningAlgorithm(String algorithm) {
        this.signatureAlgorithm = algorithm;
        return this;
    }

    public CertIssuer withIssuerCertAndKeyPair(X509Certificate issuerCert, KeyPair issuerKeyPair) {
        this.issuerDN = issuerCert.getSubjectX500Principal().getName();
        this.signingKeypair = issuerKeyPair;
        new CertVerifier(issuerCert).matchesPublicKey(issuerKeyPair.getPublic());
        return this;
    }

    public CertIssuer withIssuerDN(String issuerDN) throws InvalidNameException {
        this.issuerDN = issuerDN;
        CertUtil.parseRDN(issuerDN, true);
        return this;
    }

    // Certificate
    public CertIssuer withSubjectDN(String subjectDN) throws InvalidNameException {
        this.subjectDN = subjectDN;
        CertUtil.parseRDN(subjectDN, true);
        return this;
    }

    public CertIssuer withSerialNumber(BigInteger serialNumber) {
        this.certSerialNumber = serialNumber;
        return this;
    }

    private CertIssuer withNotBeforeDate(Date notBeforeDate) {
        this.notBeforeDate = notBeforeDate;
        return this;
    }

    private CertIssuer withLifetimeInYears(int timeInYears) {
        this.lifetimeInYears = timeInYears;
        return this;
    }

    private CertIssuer generateKeyPair() throws NoSuchAlgorithmException {
        this.signingKeypair = new KeyBuilder().size(keySize).type("RSA").buildKeyPair();
        return this;
    }

    private void updateDates() {
        if (this.notBeforeDate == null) {
            this.notBeforeDate = new Date(System.currentTimeMillis());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(notBeforeDate);
        calendar.add(Calendar.YEAR, lifetimeInYears);
        this.notAfterDate = calendar.getTime();
    }

    private void calculateSerial() {
        if (this.certSerialNumber == null) {
            this.certSerialNumber = BigInteger.valueOf(System.currentTimeMillis());
        }
    }

    public X509Certificate signCertificate(String principalName, PublicKey certPublicKey, BigInteger _serialNr, Date _notBeforeDate, int _timeInYears) throws OperatorCreationException, CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
        return this.withSerialNumber(_serialNr)
                .withNotBeforeDate(_notBeforeDate)
                .withLifetimeInYears(_timeInYears)
                .signCertificate(principalName, certPublicKey);
    }

    public CertificateKeyPair selfSign() throws OperatorCreationException, CertificateException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        // Attributes
        X500Principal issuerPrincipal = new X500Principal(subjectDN);
        updateDates();
        calculateSerial();
        X509Certificate cert = _sign(issuerPrincipal, certSerialNumber, notBeforeDate, notAfterDate, issuerPrincipal, signingKeypair.getPublic());
        return new CertificateKeyPair(cert, signingKeypair);
    }

    public X509Certificate signCertificate(String principalName, PublicKey certPublicKey) throws OperatorCreationException, CertificateException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException {
        X500Principal subjectPrincipal = new X500Principal(principalName);
        X500Principal issuerPrincipal = new X500Principal(issuerDN);
        updateDates();
        calculateSerial();
        return _sign(issuerPrincipal, certSerialNumber, notBeforeDate, notAfterDate, subjectPrincipal, certPublicKey);
    }

    private X509Certificate _sign(X500Principal _issuerPrincipal, BigInteger _serial, Date _notBefore, Date _notAfter, X500Principal _subjectPrincipal, PublicKey _subjectPubKey) throws OperatorCreationException, CertificateException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
                _issuerPrincipal,
                _serial,
                _notBefore,
                _notAfter,
                _subjectPrincipal,
                _subjectPubKey);

        if (isRoot) {
            // is CA:
            BasicConstraints basicConstraints = new BasicConstraints(65536);
            certificateBuilder.addExtension(Extension.basicConstraints, true, basicConstraints);
        }

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm)
                .setProvider(provider).build(signingKeypair.getPrivate());

        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider(provider)
                .getCertificate(certificateBuilder.build(contentSigner));

        certificate.verify(signingKeypair.getPublic());
        return certificate;
    }

    public record CertificateKeyPair(X509Certificate publicCertificate, KeyPair keyPair) {
    }

}
