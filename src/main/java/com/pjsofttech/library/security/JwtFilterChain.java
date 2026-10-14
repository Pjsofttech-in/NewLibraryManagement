package com.pjsofttech.library.security;

import com.pjsofttech.library.service.impl.UserDetailsServiceImpl;
import com.pjsofttech.library.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilterChain extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);

                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    // ✅ First try the library's own user database
                    UserDetails userDetails = null;
                    try {
                        userDetails = userDetailsService.loadUserByUsername(username);
                    } catch (Exception e) {
                        // ✅ User not found in library DB — this is an external token
                        // (from ClientSuperAdmin / SuperAdmin). Trust it since
                        // the shared JWT secret already validated the signature.
                        userDetails = null;
                    }

                    if (userDetails != null) {
                        // Library's own user — validate normally
                        if (jwtUtil.validateToken(userDetails, username, token)) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    } else {
                        // External user from host system — grant all library authorities
                        List<GrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_STAFF"),
                                new SimpleGrantedAuthority("BOOK_READ"),
                                new SimpleGrantedAuthority("BOOK_CREATE"),
                                new SimpleGrantedAuthority("BOOK_UPDATE"),
                                new SimpleGrantedAuthority("BOOK_DELETE"),
                                new SimpleGrantedAuthority("AUTHOR_READ"),
                                new SimpleGrantedAuthority("AUTHOR_CREATE"),
                                new SimpleGrantedAuthority("AUTHOR_UPDATE"),
                                new SimpleGrantedAuthority("AUTHOR_DELETE"),
                                new SimpleGrantedAuthority("CATEGORY_READ"),
                                new SimpleGrantedAuthority("CATEGORY_CREATE"),
                                new SimpleGrantedAuthority("CATEGORY_UPDATE"),
                                new SimpleGrantedAuthority("CATEGORY_DELETE"),
                                new SimpleGrantedAuthority("PUBLISHER_READ"),
                                new SimpleGrantedAuthority("PUBLISHER_CREATE"),
                                new SimpleGrantedAuthority("PUBLISHER_UPDATE"),
                                new SimpleGrantedAuthority("PUBLISHER_DELETE"),
                                new SimpleGrantedAuthority("RACK_READ"),
                                new SimpleGrantedAuthority("RACK_CREATE"),
                                new SimpleGrantedAuthority("RACK_UPDATE"),
                                new SimpleGrantedAuthority("RACK_DELETE"),
                                new SimpleGrantedAuthority("LOAN_READ"),
                                new SimpleGrantedAuthority("LOAN_CREATE"),
                                new SimpleGrantedAuthority("LOAN_RETURN"),
                                new SimpleGrantedAuthority("LOAN_RENEW"),
                                new SimpleGrantedAuthority("LOAN_MANAGE"),
                                new SimpleGrantedAuthority("FINE_READ"),
                                new SimpleGrantedAuthority("FINE_PAY"),
                                new SimpleGrantedAuthority("FINE_WAIVE"),
                                new SimpleGrantedAuthority("FINE_MANAGE"),
                                new SimpleGrantedAuthority("RESERVATION_READ"),
                                new SimpleGrantedAuthority("RESERVATION_CREATE"),
                                new SimpleGrantedAuthority("RESERVATION_CANCEL"),
                                new SimpleGrantedAuthority("RESERVATION_MANAGE"),
                                new SimpleGrantedAuthority("ACADEMIC_YEAR_READ"),
                                new SimpleGrantedAuthority("ACADEMIC_YEAR_CREATE"),
                                new SimpleGrantedAuthority("ACADEMIC_YEAR_UPDATE"),
                                new SimpleGrantedAuthority("USER_READ"),
                                new SimpleGrantedAuthority("USER_CREATE"),
                                new SimpleGrantedAuthority("USER_UPDATE"),
                                new SimpleGrantedAuthority("USER_DELETE"),
                                new SimpleGrantedAuthority("USER_MANAGE_ROLE"),
                                new SimpleGrantedAuthority("USER_MANAGE_STATUS"),
                                new SimpleGrantedAuthority("REPORT_VIEW"),
                                new SimpleGrantedAuthority("ROLE_ADMIN"),
                                new SimpleGrantedAuthority("ROLE_LIBRARIAN")
                        );

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                // Invalid token — let Spring Security handle it as 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // clear context and continue — Security will return 401 if endpoint needs auth
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}