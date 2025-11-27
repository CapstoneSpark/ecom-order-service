//package com.example.order.security;
//
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//
//@Component
//public class JwtUtils {
//
//    @Value("${app.jwtSecret}")
//    private String jwtSecret;
//
//    /**
//     * Create HMAC key for JJWT 0.12.x
//     */
//    private SecretKey getSigningKey() {
//        byte[] keyBytes;
//
//        // Try Base64 first
//        try {
//            keyBytes = Base64.getDecoder().decode(jwtSecret);
//        } catch (Exception e) {
//            keyBytes = jwtSecret.getBytes();
//        }
//
//        return new SecretKeySpec(keyBytes, "HmacSHA256");
//    }
//
//    /**
//     * Parse full Claims object using 0.12.x API
//     */
//    private Claims parseAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey())
//                .build()
//                .parseSignedClaims(token)  // <---- CORRECT FOR 0.12.x
//                .getPayload();
//    }
//
//    public String getUserNameFromJwtToken(String token) {
//        return parseAllClaims(token).getSubject();
//    }
//
//    public Long getUserIdFromJwtToken(String token) {
//        Object value = parseAllClaims(token).get("userId");
//
//        if (value instanceof Number) {
//            return ((Number) value).longValue();
//        }
//
//        try {
//            return Long.parseLong(value.toString());
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public List<String> getRolesFromJwtToken(String token) {
//        List<String> roles = new ArrayList<>();
//
//        Object raw = parseAllClaims(token).get("roles");
//        if (raw == null) return roles;
//
//        if (raw instanceof List<?> list) {
//            for (Object r : list) roles.add(String.valueOf(r));
//        } else {
//            roles.add(raw.toString());
//        }
//
//        return roles;
//    }
//
//    public boolean validateJwtToken(String token) {
//        try {
//            parseAllClaims(token);  // If parsing Works → Token Valid
//            return true;
//        } catch (Exception e) {
//            System.out.println("❌ Invalid JWT: " + e.getMessage());
//            return false;
//        }
//    }
//}
