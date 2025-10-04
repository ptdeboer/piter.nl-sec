/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.entities.authority;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Not Used:
 * User -> {Role}* -> {Authorities}** mapping.
 */
@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@Table(name = "USER_ROLES")
public class UserRole {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "role_name")
    private String roleName;

    /**
     * List of Authorities this UserRole provides.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role_authorities",
            joinColumns = {@JoinColumn(name = "user_role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private List<Authority> authorities;

    protected UserRole() {
    }

}
