package com.udaya.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udaya.response.BaseResponse;
import com.udaya.security.CustomUserDetails;
import com.udaya.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final List<String> PUBLIC_URLS = List.of("/swagger-ui.html", "/swagger-ui", "/v3/api-docs", "/api-docs", "/swagger-resources", "/webjars", "/auth/login", "/auth/register", "/actuator");
	private final ObjectMapper objectMapper;
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		// * Skip public URLs
		if (PUBLIC_URLS.stream().anyMatch(request.getServletPath()::startsWith)) {
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			sendError(response, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header", request);
			return;
		}

		try {
			String token = authHeader.substring(7);
			String username = jwtUtil.extractUsername(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				if (jwtUtil.isTokenValid(token, username)) {
					// * Extract userId and groups from JWT
					Long userId = jwtUtil.extractUserId(token);
					List<String> groups = jwtUtil.extractGroups(token);
					if (groups == null) groups = Collections.emptyList();

					CustomUserDetails userDetails = new CustomUserDetails(userId, username, "", groups, true);

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			sendError(response, HttpStatus.UNAUTHORIZED, "Token verification failed: " + e.getMessage(), request);
		}
	}

	private void sendError(HttpServletResponse response, HttpStatus status, String message, HttpServletRequest request) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		BaseResponse<Object> apiError = BaseResponse.error(status, message);
		response.getWriter().write(objectMapper.writeValueAsString(apiError));
	}
}
