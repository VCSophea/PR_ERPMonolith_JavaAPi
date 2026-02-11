package com.udaya.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udaya.exception.ApiError;
import com.udaya.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    // * Public Endpoints
    private static final List<String> PUBLIC_URLS = List.of(
            "/swagger-ui.html", "/swagger-ui", "/v3/api-docs", "/api-docs",
            "/swagger-resources", "/webjars", "/api/v1/auth/login",
            "/actuator"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // * Skip Public URLs
        if (PUBLIC_URLS.stream().anyMatch(request.getRequestURI()::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // * Extract & Validate Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpStatus.UNAUTHORIZED, "Missing or Invalid Token", request);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            
            if (username == null || username.isEmpty()) throw new Exception("Invalid Token Subject");
            
            // * Token Valid -> Proceed
            filterChain.doFilter(request, response);
        } catch (Exception e) {
             sendError(response, HttpStatus.UNAUTHORIZED, "Token Verification Failed: " + e.getMessage(), request);
        }
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message, HttpServletRequest request) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiError apiError = new ApiError(status, message, request.getRequestURI());
        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
