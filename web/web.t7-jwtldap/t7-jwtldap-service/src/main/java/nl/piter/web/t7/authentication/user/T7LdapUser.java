/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
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
