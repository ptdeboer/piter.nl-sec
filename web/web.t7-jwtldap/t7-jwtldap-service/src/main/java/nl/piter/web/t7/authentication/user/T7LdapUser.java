/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.authentication.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for REST calls converting LDAP data.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class T7LdapUser {
    private String userName;
    private String principalName;
    private String fullName;
    private String email;
    private List<String> memberShips;
}
