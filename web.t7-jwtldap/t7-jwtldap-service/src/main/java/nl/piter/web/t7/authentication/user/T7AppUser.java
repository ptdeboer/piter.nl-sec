/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * Application User (AppUser) implementing Spring Security UserDetails.
 */
public class T7AppUser implements UserDetails {

    private final Long id;
    private final String username;
    private final String fullname;
    private final String password;
    private final String email;
    private final boolean localUser;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public T7AppUser(
            Long id,
            String username,
            String fullname,
            String email,
            String password,
            boolean localUser,
            Collection<? extends GrantedAuthority> authorities,
            boolean enabled

    ) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
        this.localUser = localUser;
    }

    @JsonIgnore
    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * These are the combined Granted Authorities this user has.
     * This is a merge from authorities retrieved from local stored UserRoles and LdapRoles.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // === Custom Attributes === //

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public boolean isLocalUser() {
        return localUser;
    }

    @Override
    @SuppressWarnings("squid:S2068") // 'password'
    public String toString() {
        return "T7User:[" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullname='" + fullname + '\'' +
                ", password='" + (StringUtils.hasText(password) ? "<YES>" : "<NO>") + '\'' +
                ", email='" + email + '\'' +
                ", localUser=" + localUser +
                ", authorities=" + authorities +
                ", enabled=" + enabled +
                ']';
    }

}
