//package com.example.order.security;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
///**
// * JWT Authentication Filter for Product/Cart/Order Services
// *
// * Extracts username, userId, and roles from JWT token.
// * NO database call - all info comes from token!
// *
// * This makes authentication stateless and fast.
// */
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    /**
//     * Extract JWT token from Authorization header
//     */
//    private String parseJwt(HttpServletRequest request) {
//        String headerAuth = request.getHeader("Authorization");
//        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
//            return headerAuth.substring(7);
//        }
//        return null;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//                                    throws ServletException, IOException {
//        try {
//            String jwt = parseJwt(request);
//
//            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//                // Extract user info from token (NO DATABASE CALL!)
//                String username = jwtUtils.getUserNameFromJwtToken(jwt);
//                Long userId = jwtUtils.getUserIdFromJwtToken(jwt);
//                List<String> roles = jwtUtils.getRolesFromJwtToken(jwt);
//
//                // Convert roles to Spring Security authorities
//                List<SimpleGrantedAuthority> authorities = roles.stream()
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//
//                // Create authentication object
//                UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                        username,      // Principal (email/username)
//                        null,          // Credentials (not needed)
//                        authorities    // Authorities (roles from token)
//                    );
//
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                // Set in security context
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                // Optional: Log for debugging (remove in production)
//                System.out.println("🔓 Authenticated: " + username + " (ID: " + userId + ") with roles: " + roles);
//            }
//        } catch (Exception e) {
//            logger.error("Cannot set user authentication: " + e.getMessage(), e);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
