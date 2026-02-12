package com.udaya.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

	@Value("${jwt.secret-key}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private long jwtExpiration;

	// * Generate JWT with userId, username, role, and groups
	public String generateToken(Long userId, String username, String role, java.util.List<String> groups) {
		return Jwts.builder().subject(username).claim("userId", userId).claim("role", role).claim("groups", groups).issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + jwtExpiration)).signWith(getSigningKey()).compact();
	}

	public String generateToken(String username, String role, java.util.List<String> groups) {
		return generateToken(null, username, role, groups);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public Long extractUserId(String token) {
		Number userId = extractClaim(token, claims -> claims.get("userId", Number.class));
		return userId != null ? userId.longValue() : null;
	}

	@SuppressWarnings("unchecked")
	public java.util.List<String> extractGroups(String token) {
		return extractClaim(token, claims -> claims.get("groups", java.util.List.class));
	}

	public boolean isTokenValid(String token, String username) {
		return extractUsername(token).equals(username) && !isTokenExpired(token);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		return claimsResolver.apply(extractAllClaims(token));
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}
}
