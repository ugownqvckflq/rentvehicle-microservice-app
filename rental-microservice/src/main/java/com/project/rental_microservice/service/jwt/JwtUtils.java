package com.project.rental_microservice.service.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;

public class JwtUtils {

    private static String jwtSecret = "jsuaiqlsmdjkalskdjkjahjdjsdyuakkajsjdhuasnbdjadyaysdhkasjdhjsdugjkksas";

    public static Long getUserIdFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public static String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

}
