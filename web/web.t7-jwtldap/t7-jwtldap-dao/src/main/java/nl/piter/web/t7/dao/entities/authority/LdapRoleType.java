/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
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
