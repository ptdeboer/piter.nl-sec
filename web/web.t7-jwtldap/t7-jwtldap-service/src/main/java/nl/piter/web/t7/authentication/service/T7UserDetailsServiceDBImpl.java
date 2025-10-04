/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.authentication.service;

import com.google.common.base.Strings;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.authentication.user.T7AppUser;
import nl.piter.web.t7.authentication.user.T7AppUserMapper;
import nl.piter.web.t7.dao.UserAuthoritiesDao;
import nl.piter.web.t7.dao.entities.authority.Authority;
import nl.piter.web.t7.dao.entities.authority.User;
import nl.piter.web.t7.dao.entities.authority.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * User Authentication and Authorization.
 * Resolves usernames and checks their authorized roles and optionally stores/caches them in a persistent user details DB.
 */
@Service
@Slf4j
public class T7UserDetailsServiceDBImpl implements T7UserDetailsService {

    @Value("${webapp.authentication.local.admin.username}")
    private String defaultAdminUsername;

    @Value("${webapp.authentication.local.admin.password}")
    private String defaultAdminPassword;

    @Value("#{'${webapp.authentication.local.admin.authorities}'.split(',')}")
    private List<String> defaultAdminAuthorities;

    @Value("${webapp.authentication.local.admin.enabled}")
    private Boolean defaultAdminEnabled;

    @Autowired
    private UserAuthoritiesDao userDao;

    @Override
    public T7AppUser loadUserByUsername(String username) {
        log.debug("loadUserByUsername():username={}", username);

        User user = userDao.findByUsername(username);
        if (user != null) {
            return T7AppUserMapper.toT7User(user);
        }

        // Check if 'default' user  (local admin) is enabled.
        T7AppUser userDetails = checkDefaultUser(username);
        if (userDetails != null) {
            return userDetails;
        } else {
            log.info("Username not found in local configuration nor in persistent userstore:{}", username);
            return null;
        }
    }

    @Transactional
    public T7AppUser saveUserDetails(T7AppUser userDetails) {
        List<String> auths = T7AppUserMapper.toStringList(userDetails.getAuthorities());
        // Optional: Here a merge is possible with a previous stored user and new savedUser:
        User savedUser = saveUser(userDetails, auths);
        return T7AppUserMapper.toT7User(savedUser);
    }

    /**
     * Check for non ldap local (admin) user.
     * This could be for example a local admin which needs to be able to access the service without an LDAP account.
     */
    private T7AppUser checkDefaultUser(String username) {
        if (Strings.isNullOrEmpty(username)) {
            return null;
        }

        if (username.compareTo(this.defaultAdminUsername) != 0) {
            return null;
        }

        // Map local user (admin) roles to local role authorities
        log.warn("Warning: using default user from properties file:'{}' with roles:'{}'", this.defaultAdminUsername, defaultAdminAuthorities);

        List<String> localAuths = defaultAdminAuthorities;

        if (isEmpty(localAuths)) {
            log.error("Explicit configured default (local) user has no authorities mapped for user: {}", username);
        } else {
            log.warn("Warning: mapped default local user local authorities {} = {}", username, defaultAdminAuthorities);
        }

        // Map default roles to authorities, these MUST exist in the DB.
        return T7AppUserMapper.createT7User(null,
                this.defaultAdminUsername,
                "Default Admin user:" + this.defaultAdminUsername,
                null,
                this.defaultAdminPassword,
                true,
                localAuths,
                this.defaultAdminEnabled);
    }

    @Transactional
    public User saveUser(T7AppUser user, List<String> cachedAuthorities) {
        log.info("Saving/updating user configuration in persistent userstore for:'{}'", user);

        // Create or update user:
        User userEntity = userDao.findByUsername(user.getUsername());
        if (userEntity == null) {
            log.warn("Creating new user configuration in persistent userstore for:'{}'", user);
            userEntity = User.builder()
                    .id(null)
                    .username(user.getUsername())
                    .fullname(user.getFullname())
                    .email(user.getEmail())
                    .enabled(true).build(); // passed authentication -> enabled by default.
        }

        // Create or update User Authorities: User <- [N:M] -> Authorities
        List<Authority> userAuthEntities = new ArrayList<>();

        // Save optional new Authorities:
        for (String auth : cachedAuthorities) {
            Authority authEntity = userDao.findByAuthorityName(auth);
            Authority verifiedAuthEntity = authEntity;
            if (authEntity == null) {
                verifiedAuthEntity = this.userDao.save(Authority.builder().authorityName(auth).build());
            }
            userAuthEntities.add(verifiedAuthEntity);
        }
        userEntity.setCachedAuthorities(userAuthEntities);
        return this.userDao.save(userEntity);
    }

    @Override
    public List<String> getUserRoles(String username) {
        List<UserRole> roles = this.userDao.listUserRolesForUser(username);
        log.debug("getUserRoles(): {} ={}", username, roles);
        if (roles == null) {
            return new ArrayList<>();
        }
        return roles.stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toList());
    }

    @Override
    public List<T7AppUser> getUsers() {
        List<User> users = this.userDao.listUsers();
        log.debug("getUsers(): {}", users);
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
                .map(T7AppUserMapper::toT7User)
                .collect(Collectors.toList());
    }
}
