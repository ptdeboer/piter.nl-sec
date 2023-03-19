/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao.entities.authority;

public enum LdapRoleType {
    /**
     * LDAP 'memberOf' relation
     */
    LDAP_MEMBEROF;

    public static boolean contains(String name) {
        try {
            return (LdapRoleType.valueOf(name) != null);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
