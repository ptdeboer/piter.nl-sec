package nl.piter.web.t6.util;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.exceptions.ServiceSecurityException;
import org.slf4j.Logger;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

@Slf4j
// Not autowired so it can be used during pre-configuration stage:
public class CertUtil {

    private static CertUtil instance;

    public static CertUtil certUtil() {
        // Lazy init:
        if (instance == null) {
            instance = new CertUtil();
        }
        return instance;
    }

    private CertUtil() {
    }

    public String extractCN(X509Certificate clientCert) {
        final X500Principal principle = clientCert.getSubjectX500Principal();
        log.debug("x509Principle name: {}", principle.getName());

        LdapName ldapName;
        try {
            ldapName = new LdapName(principle.getName());
        } catch (InvalidNameException e) {
            throw new ServiceSecurityException("Failed to parse SubjectDN:" + e.getMessage(), e);
        }

        List<Rdn> rdns = ldapName.getRdns();
        rdns.stream()
                .forEach(el -> log.debug("-RDN '{}'='{}", el.getType(), el.getValue()));

        Optional<Rdn> optCN = rdns.stream().filter(rdn -> rdn.getType().equalsIgnoreCase("CN")).findFirst();
        Optional<Rdn> optOU = rdns.stream().filter(rdn -> rdn.getType().equalsIgnoreCase("OU")).findFirst();

        log.debug("x509Principle CN  : {}", optCN);
        log.debug("x509Principle OU  : {}", optOU);

        if (!optCN.isPresent()) {
            throw new ServiceSecurityException("SubjectDN doesn't have CN field:" + ldapName);
        }
        return optCN.get().getValue().toString();
    }

    public void logCertInfo(Logger log, X509Certificate cert) {
        log.info("cert.subject   : '{}' (serial:#{})", cert.getSubjectX500Principal().getName(), cert.getSerialNumber());
        log.info("cert.issuer    : '{}'", cert.getIssuerX500Principal().getName());
        log.info("cert.notBefore : '{}'", cert.getNotBefore());
        log.info("cert.notAfter  : '{}'", cert.getNotAfter());
    }

}
