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

/**
 * Granted (Role) Authorities, for example "EDITOR" or "ADMIN".
 * A User can have many Roles where each role can map onto a set of Authorities.
 * User -> {Role}* -> {Authority}**
 */
@Entity
@Builder
@Table(name = "AUTHORITIES")
@AllArgsConstructor
public class Authority {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "authority_seq")
    @SequenceGenerator(name = "authority_seq", sequenceName = "authority_seq", allocationSize = 1)
    private Long id;

    @Column(name = "authorityName", length = 64)
    @NotNull
    private String authorityName;

    protected Authority() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    @Override
    public String toString() {
        return "<Authority>:["
                + "id:" + id
                + ",authorityName:" + authorityName
                + ']';
    }
}

