/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.ldap;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.config.LdapConfig;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Custom LDAP Client  allow for more control into the LDAP queries.
 * (Spring has also one, but that one isn't used here).
 * Note: Manual configuration may differ from Spring Configuration.
 */
@Slf4j
public class LdapClient {

    private final LdapTemplate ldapTemplate;
    private final LdapConfig ldapConfig;

    public LdapClient(LdapTemplate ldapTemplate, LdapConfig ldapConfig) {
        this.ldapTemplate = ldapTemplate;
        this.ldapConfig = ldapConfig;
    }

    public boolean authenticate(String user, String passwd) {
        return authenticate(user, passwd, ldapConfig.getLdapAccountType());
    }

    public boolean authenticate(String user, String passwd, LdapAccountType ldapAccountType) {
        Filter filter = createUserFilter(user, ldapAccountType);
        boolean authenticated = ldapTemplate.authenticate(ldapConfig.getUserSearchBase(), filter.encode(), passwd);
        log.debug("authenticate(): userFilter='{}', user='{}' => {}", filter, user, authenticated);
        return authenticated;
    }

    private Filter createUserFilter(String user, LdapAccountType ldapAccountType) {
        return new EqualsFilter(ldapAccountType.getUserQueryField(), user);
    }

    public List<LdapPerson> getAllPersonNames() {
        return ldapTemplate.search(
                query().where("objectclass").is("person"), this::mapLdapPerson);
    }

    public LdapPerson queryUser(String username) {
        return queryUser(username, ldapConfig.getLdapAccountType());
    }

    public LdapPerson queryUser(String user, LdapAccountType accountType) {
        Filter filter = createUserFilter(user, accountType);
        log.debug("Querying LDAP for user='{}', with filter='{}'", user, filter);
        List<LdapPerson> persons = ldapTemplate.search(
                query().filter(filter), this::mapLdapPerson);

        if (Collections.isEmpty(persons)) {
            return null;
        }

        return persons.get(0);
    }

    /**
     * Maps LDAP attributes to LdapPerson Object.
     * For example:
     * <ul>
     *     <li> 'cn' => Full Name</li>
     *     <li> 'gn' => First or 'given' name
     *     <li> 'sn' => Surname (Family name)
     * </ul>
     * Full or Common Name = 'cn', First or Given name is ='gn', Surname = 'sn:'
     *
     * @throws NamingException
     */
    private LdapPerson mapLdapPerson(Attributes attrs) throws NamingException {
        String userName = attrToString(attrs.get(ldapConfig.getLdapAccountType().getUserQueryField()));
        String principalName = attrToString(attrs.get("userPrincipalName"));
        String fullName = attrs.get("cn").get().toString();
        List<String> memberShips = new ArrayList<>();
        Attribute memberOfAttr = attrs.get("memberOf");
        if (memberOfAttr != null) {
            NamingEnumeration<?> memberEnums = memberOfAttr.getAll();
            while (memberEnums.hasMore()) {
                Object value = memberEnums.next();
                memberShips.add(value.toString());
            }
        } else {
            log.warn("Account has no ldap 'memberOf' roles: {}", userName);
        }
        return new LdapPerson(userName, principalName, fullName, memberShips);
    }

    private String attrToString(Attribute attr) throws NamingException {
        if (attr == null) {
            return null;
        }
        Object value = attr.get();
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
