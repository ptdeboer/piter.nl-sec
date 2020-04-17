/* (C) 2020 Piter.NL */
package nl.piter.web.t6.secure;

import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t6.exceptions.ServiceSecurityException;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SubjectX509PrincipalExtractor implements X509PrincipalExtractor {

    /**
     * Inspect X509 and map to User Principle.
     * Currently returns "CN = ..."  field.
     *
     * @param clientCert
     * @return CN part of SubjectDN
     */
    @Override
    public String extractPrincipal(X509Certificate clientCert) {
        final X500Principal principle = clientCert.getSubjectX500Principal();
        log.debug("x509Principle name: {}", principle.getName());

        LdapName ldapName;
        try {
            ldapName = new LdapName(principle.getName());
        } catch (InvalidNameException e) {
            throw new ServiceSecurityException("Failed to parse SubjectDN:" + e.getMessage(), e);
        }

        List<Rdn> rdns = ldapName.getRdns();
        rdns.stream().forEach(el -> log.debug("-RDN '{}'='{}", el.getType(), el.getValue()));

        Optional<Rdn> optCN = rdns.stream().filter(rdn -> rdn.getType().equalsIgnoreCase("CN")).findFirst();
        Optional<Rdn> optOU = rdns.stream().filter(rdn -> rdn.getType().equalsIgnoreCase("OU")).findFirst();

        log.debug("x509Principle CN  : {}", optCN);
        log.debug("x509Principle OU  : {}", optOU);

        if (!optCN.isPresent()) {
            throw new ServiceSecurityException("SubjectDN doesn't have CN field:" + ldapName);
        }
        return optCN.get().getValue().toString();
    }

}
