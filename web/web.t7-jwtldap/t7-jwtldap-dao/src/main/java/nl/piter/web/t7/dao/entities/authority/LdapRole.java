/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
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
 * Table to map an LDAP role to actual Authorities.
 * The term LDAP 'role' is an abstraction here. Is it a relation to a group or domain.
 * The default 'role' is 'member/memberOf' LDAP relation.
 * An authenticated (ldap) user maps onto a role which maps to a set of authorities.
 * LdapUser (DN) -> {LdapRole}* -> {Authorities}**
 */
@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@Table(name = "LDAP_ROLES")
public class LdapRole {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The LDAP role name typically is the distinguished name or 'dn:' part.<br>
     * A type could be added here as follows:
     * <pre>
     * private RoleNameType roleNameType=RoleNameType.DN;
     * </pre>
     */
    @NotNull
    @Column(name = "role_name")
    private String roleName;

    /**
     * Defaults to 'memberOf' relation.
     */
    @NotNull
    @Column(name = "role_type")
    @Enumerated(EnumType.STRING)
    private LdapRoleType roleType;

    /**
     * Many Ldap Roles map to Many Authorities
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ldap_role_authorities",
            joinColumns = {@JoinColumn(name = "ldap_role_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private List<Authority> authorities;

    protected LdapRole() {
    }

}
