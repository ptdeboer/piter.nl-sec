/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.ldap;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Builder
public class LdapPerson {

    private final String userName;
    private final String principalName;
    private final String fullName;
    @NotNull // empty array if null
    private final List<String> memberShips;

    public LdapPerson(String userName, String principalName, String fullName, List<String> memberShips) {
        this.userName = userName;
        this.principalName = principalName;
        this.fullName = fullName;
        this.memberShips = (memberShips != null) ? memberShips : new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public String getFullName() {
        return fullName;
    }

    public List<String> getMemberShips() {
        return memberShips;
    }

    public String toString() {
        return "<LdapPerson>:{"
                + "userName:'" + userName
                + ",principalName:'" + principalName
                + ",fullName:'" + fullName + "'"
                + ",memberShips:" + Arrays.toString(memberShips.toArray())
                + "}";
    }

}
