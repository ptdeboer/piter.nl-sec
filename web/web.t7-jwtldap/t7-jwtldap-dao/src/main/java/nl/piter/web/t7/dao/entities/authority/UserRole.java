/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao.entities.authority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
@Table(name = "user_roles")
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
