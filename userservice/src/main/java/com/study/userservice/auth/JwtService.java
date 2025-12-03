package com.study.userservice.auth;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  //  @Value("${application.security.jwt.secret-key}") // add secret
  //  private String secretKey;

  //  @Value("${application.security.jwt.expiration}")
  //  private long jwtExpiration;

  //  @Value("${application.security.jwt.refresh-token.expiration}")
  //  private long refreshExpiration;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractRole(String token) {
    final Claims claims = extractAllClaims(token);
    String r = claims.get("Role").toString();
    return r;
  }

  public String extractUserId(String token) {
    final Claims claims = extractAllClaims(token);
    String id = claims.get("UserId").toString();
    return id;
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {

    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  Key getSignInKey() {

    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    String key = dotenv.get("MY_SECRET_KEY");

    //    byte[] keyBytes = Decoders.BASE64.decode(key);

    String str = new String(Decoders.BASE64.decode(key));

    byte[] keyBytes = str.getBytes();

    return Keys.hmacShaKeyFor(keyBytes);
  }
}
