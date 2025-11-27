//package com.example.order.Config;
//
//import java.util.Arrays;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import com.example.order.security.JwtAuthenticationEntryPoint;
//import com.example.order.security.JwtAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Autowired
//    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//            .csrf(csrf -> csrf.disable())
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .sessionManagement(session ->
//                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )
//            .exceptionHandling(ex ->
//                    ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
//            )
//            .authorizeHttpRequests(auth -> {
//
//                // Public (Swagger + Health)
//                auth.requestMatchers(getPublicEndpoints()).permitAll();
//
//                // Admin-only
//                auth.requestMatchers("/api/v1/orders/admin/**").hasRole("ADMIN");
//
//                // Everything else requires authentication
//                auth.anyRequest().authenticated();
//            })
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    /**
//     * Public endpoints for ORDER SERVICE
//     */
//    protected String[] getPublicEndpoints() {
//        return new String[]{
//            "/api/health",
//            "/actuator/health",
//
//            // Swagger UI
//            "/swagger-ui/**",
//            "/swagger-ui.html",
//            "/v3/api-docs/**",
//            "/swagger-resources/**",
//            "/webjars/**"
//        };
//    }
//
//    /**
//     * CORS configuration
//     */
//    protected CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList(
//            "http://localhost:3000",
//            "http://localhost:5173",
//            "http://localhost:8080"
//        ));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
