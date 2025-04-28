package com.coworking.coworking_booking_system.security;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}")
    private int jwtExpirationMs;

    // Method to generate a JWT token from Authentication object (Updated for
    // 0.12.x)
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Build and return the JWT using updated methods
        return Jwts.builder()
                .subject(userPrincipal.getUsername()) // Use subject() instead of setSubject()
                .issuedAt(now) // Use issuedAt() instead of setIssuedAt()
                .expiration(expiryDate) // Use expiration() instead of setExpiration()
                .signWith(key()) // Pass the SecretKey directly, algorithm is inferred
                .compact();
    }

    // Get the signing key (No changes needed here)
    private SecretKey key() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes); // Generates a key suitable for HMAC-SHA algorithms (like HS512)
    }

    // Extract username from a JWT token (Updated for 0.12.x)
    public String getUserNameFromJwtToken(String token) {
        // Use Jwts.parser() and new methods
        Claims claims = Jwts.parser()
                .verifyWith(key()) // Use verifyWith() instead of setSigningKey()
                .build()
                .parseSignedClaims(token) // Use parseSignedClaims() instead of parseClaimsJws()
                .getPayload(); // Use getPayload() instead of getBody()

        return claims.getSubject();
    }

    // Validate a JWT token (Updated for 0.12.x)
    public boolean validateJwtToken(String authToken) {
        try {
            // Use Jwts.parser() and new methods for validation
            Jwts.parser()
                    .verifyWith(key()) // Use verifyWith()
                    .build()
                    .parseSignedClaims(authToken); // Use parseSignedClaims()
            return true;
        } catch (SignatureException e) { // Keep specific exception catches
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // This might indicate the token string itself is null or empty before parsing
            log.error("JWT claims string is empty or invalid: {}", e.getMessage());
        }
        // SecurityException is less common now unless there are underlying provider
        // issues

        return false; // Token is invalid if any exception occurred
    }

}
