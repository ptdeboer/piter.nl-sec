/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.dao.entities.authority;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(name = "username", length = 64, unique = true)
    @NotNull
    @Size(min = 1, max = 96) // reduced minimum to 2 to support 'jan', 'ed', 'li' and 'X' ;-).
    private String username;

    @Column(name = "password", length = 128)
    private String password;

    @Column(name = "fullname", length = 128)
    private String fullname;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "enabled")
    @NotNull
    private Boolean enabled;

    @Column(updatable = false)
    private LocalDateTime creationDate;

    private LocalDateTime modifiedDate;

    /**
     * User level Authorities (without roles)
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authorities",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private List<Authority> cachedAuthorities;

    /**
     * Local stored User Roles. (Not used in demo, fetched from LdapRole).
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_user_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<UserRole> roles;

    protected User() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Authority> getCachedAuthorities() {
        return cachedAuthorities;
    }

    public void setCachedAuthorities(List<Authority> authorities) {
        this.cachedAuthorities = authorities;
    }

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }

}
