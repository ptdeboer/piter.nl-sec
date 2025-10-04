/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.ldap.LdapAccountType;
import nl.piter.web.t7.ldap.LdapClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Getter
@Configuration
public class LdapConfig {

    @Value("${webapp.authentication.ldap.urls}")
    private String ldapUrl;

    @Value("${webapp.authentication.ldap.base}")
    private String ldapBase;

    @Value("${webapp.authentication.ldap.userdn}")
    private String ldapUserDn;

    @Value("${webapp.authentication.ldap.password}")
    private String ldapPassword;

    @Value("${webapp.authentication.ldap.enabled}")
    private Boolean ldapEnabled;

    @Value("${webapp.authentication.ldap.userSearchBase}")
    private String userSearchBase;

    @Value("${webapp.authentication.ldap.accountType:UID}")
    private String ldapAccountTypeProperty;

    /**
     * Context used by my custom client, not Spring's.
     */
    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource ldapContextSource = new LdapContextSource();
        ldapContextSource.setUrl(ldapUrl);
        ldapContextSource.setBase(ldapBase);
        ldapContextSource.setUserDn(ldapUserDn);
        ldapContextSource.setPassword(ldapPassword);

        return ldapContextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource ldapContextSource) throws Exception {
        LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
        ldapTemplate.setIgnorePartialResultException(true);
        ldapTemplate.afterPropertiesSet();
        return ldapTemplate;
    }

    /**
     * Custom LDAP client:
     */
    @Bean
    public LdapClient ldapClient(LdapTemplate ldapTemplate) {
        return new LdapClient(ldapTemplate, this);
    }

    public String getUserSearchBase() {
        return this.userSearchBase;
    }

    public LdapAccountType getLdapAccountType() {
        // Improper defined ENUMs cause runtime errors:
        Optional<LdapAccountType> optType = Arrays.stream(LdapAccountType.values())
                .filter(val -> val.toString().equals(ldapAccountTypeProperty))
                .findFirst();

        if (optType.isPresent()) {
            log.info("Using LdapAccountType: {}", optType.get());
            return optType.get();
        }

        log.error("Default LdapAccountType not recognized or not set: {}", ldapAccountTypeProperty);
        return LdapAccountType.UID;
    }

    public void setLdapAccountType(LdapAccountType type) {
        this.ldapAccountTypeProperty = type.toString();
    }
}
