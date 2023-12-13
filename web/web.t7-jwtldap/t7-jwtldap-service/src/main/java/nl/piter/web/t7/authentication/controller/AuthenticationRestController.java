/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nl.piter.web.t7.authentication.jwt.JwtAuthenticationRequest;
import nl.piter.web.t7.authentication.jwt.JwtAuthenticationResponse;
import nl.piter.web.t7.authentication.jwt.JwtTokenUtil;
import nl.piter.web.t7.authentication.service.T7AuthenticationService;
import nl.piter.web.t7.exceptions.ServiceAuthenticationException;
import nl.piter.web.t7.ldap.LdapAccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthenticationRestController {

    final private T7AuthenticationService authenticationService;
    final private UserDetailsService userDetailsService;
    final private JwtTokenUtil jwtTokenUtil;

    @Autowired
    AuthenticationRestController(T7AuthenticationService authenticationService, UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) {
        String token;
        LdapAccountType accountType = LdapAccountType.UID;

        try {
            Authentication authentication = authenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword(), accountType);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (!authentication.isAuthenticated()) {
                log.warn("Invalid Authentication:{}", authentication);
                // If no SecurityException has been thrown yet, return http error:
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Failure.");
            } else {
                log.debug("Valid authentication={}", authentication);
            }

            // Reload UserDetails as LDAP authentication currently doesn't hold any attributes:
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            token = jwtTokenUtil.generateToken(userDetails);

            // Return the token
            return ResponseEntity.ok(new JwtAuthenticationResponse(token));
        } catch (BadCredentialsException e) {
            // Invalid user + password combination:
            log.info("BadCredentialsException for user:{}", authenticationRequest.getUsername()); // authentication audit
            throw new ServiceAuthenticationException(e.getMessage(), e);
        } catch (AuthenticationException e) {
            // Other error, log this:
            log.error("AuthenticationException", e); // authentication audit
            throw new ServiceAuthenticationException(e.getMessage(), e);
        }
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authTokenHeader = request.getHeader(this.jwtTokenUtil.getJwtConfig().getTokenHeader());
        String authToken = (authTokenHeader != null) ? jwtTokenUtil.filterTokenFromHeader(authTokenHeader) : null;
        String refreshedToken = jwtTokenUtil.refreshToken(authToken);
        return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
    }

    @RequestMapping(value = "${jwt.route.authentication.validate}", method = RequestMethod.GET)
    public ResponseEntity validateAuthenticationToken(HttpServletRequest request) {
        String authTokenHeader = request.getHeader(this.jwtTokenUtil.getJwtConfig().getTokenHeader());
        String authToken = (authTokenHeader != null) ? jwtTokenUtil.filterTokenFromHeader(authTokenHeader) : null;

        Object userDetailsObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(userDetailsObj instanceof UserDetails userDetails)) {
            log.warn("No UserDetails found. Principal Object = <{}>'{}'", userDetailsObj.getClass().getCanonicalName(), userDetailsObj);
            throw new IllegalStateException("Failed to validate UserCredentials");
        }
        if (!jwtTokenUtil.validateToken(authToken, userDetails)) {
            if (jwtTokenUtil.isTokenExpired(authToken)) {
                throw new ServiceAuthenticationException("Credentials expired for user:" + userDetails.getUsername());
            } else {
                throw new ServiceAuthenticationException("Failed to verify User Credentials for user:" + userDetails.getUsername());
            }
        }
        return ResponseEntity.ok(new JwtAuthenticationResponse(authToken));
    }


}
