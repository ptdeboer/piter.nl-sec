/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.authentication.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Http Header filter which checks for "Authorization" header with as value prefix: "Bearer".
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        public int status;
        public String error;
        public String message;
    }

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper localObjectMapper = new ObjectMapper();

    public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // Note: do not throw exceptions here unless there is some real internal error as exceptions thrown here bypass the RestResponseExceptionHandler.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String authTokenHeader = request.getHeader(this.jwtTokenUtil.getJwtConfig().getTokenHeader());
        String authToken = (authTokenHeader != null) ? jwtTokenUtil.filterTokenFromHeader(authTokenHeader) : null;
        String username = null;

        try {
            if (authToken == null) {
                log.debug("Failed to filter Token from header:'{}'.", authTokenHeader);
            } else {
                log.debug("Checking authentication token:'{}'", authToken);
                username = jwtTokenUtil.getUsernameFromToken(authToken);
                log.debug("Checking authentication for user (from token):'{}'.", username);
            }
        } catch (ExpiredJwtException e) {
            // Log, but do not throw exceptions during filter chain handling as this results in 500 and not in 401.
            log.warn("Expired credentials detected: {}", e.getMessage());
            // Create custom http error response and STOP filtering here:
            createErrorResponse(response, HttpStatus.UNAUTHORIZED, "Expired credentials detected: " + e.getMessage());
            return;
        } catch (JwtException e) {
            // Generic JWT exception:
            log.warn("Invalid credentials detected: {}", e.getMessage());
            createErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid credentials detected: " + e.getMessage());
            return;
        }

        if (username == null) {
            // Should not happen anymore, but clear context anyway.
            SecurityContextHolder.getContext().setAuthentication(null);
        } else if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check current credentials against database.
            // Optimization: could trust stored claims (authorities) from Token here instead of reload the user DB:
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            // Happens after Service reset/reload or DB has been altered.
            // Token has been generated for this user, but actual user isn't known. User is missing or has been deleted ?
            if (userDetails == null) {
                log.warn("User details not found for user taken from Authenticated Token:" + username + "'");
            }
            // Actual verification of details stored inside token with (stored) UserDetails and
            // SecurityContext is set.
            else if (!jwtTokenUtil.validateToken(authToken, userDetails)) {
                // Authenticated but not authorized:
                log.warn("User details validation failed for authenticated user: {}", username);
            } else {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                log.info("Authenticated user:'{}'", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // Even if not authenticated, continue, but without valid authentication.
        chain.doFilter(request, response);
    }

    /**
     * Exceptions thrown during filter handling will not be caught be Spring's REST Exception handler, so write custom HTTP Error response here.
     */
    private void createErrorResponse(HttpServletResponse response, HttpStatus error, String message) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(error.value(), error.getReasonPhrase(), message);
        response.setContentType("application/json");
        response.setStatus(error.value());
        response.getWriter().write(localObjectMapper.writeValueAsString(errorResponse));
    }

}
