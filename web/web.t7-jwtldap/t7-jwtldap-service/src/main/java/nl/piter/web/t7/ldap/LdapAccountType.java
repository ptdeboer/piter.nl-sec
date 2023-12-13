/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.ldap;

public enum LdapAccountType {
    UID("uid"),
    SAM("sAMAccountName"),
    EMAIL("mail");

    private final String queryField;

    LdapAccountType(String field) {
        queryField = field;
    }

    public String getUserQueryField() {
        return queryField;
    }

}
