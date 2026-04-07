package br.com.gabrielcaio.pdv.security;

import br.com.gabrielcaio.pdv.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

  private static final String SECRET = "minha-chave-super-secreta-precisa-ter-32-bytes";
  private static final long EXPIRATION = 1000 * 60 * 60; // 1h

  private Key getSignKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes());
  }

  public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getEmail())
        .claim("role", user.getRole().name())
        .claim("companyId", user.getCompany() != null ? user.getCompany().getId() : null)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaims(token).getSubject();
  }

  public boolean isValid(String token, User user) {
    String username = extractUsername(token);
    return username.equals(user.getEmail()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractClaims(token).getExpiration().before(new Date());
  }

  private Claims extractClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
