//package com.example.order.security;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
///**
// * Entry point to return 401 for unauthorized requests
// */
//@Component
//public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    @Override
//    public void commence(HttpServletRequest request,
//                         HttpServletResponse response,
//                         AuthenticationException authException)
//                         throws IOException, ServletException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json;charset=UTF-8");
//
//        String message = "{\"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\"}";
//
//        try (PrintWriter writer = response.getWriter()) {
//            writer.write(message);
//            writer.flush();
//        }
//    }
//}
