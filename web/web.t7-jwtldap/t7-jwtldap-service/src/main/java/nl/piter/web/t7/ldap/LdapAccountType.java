/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
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
