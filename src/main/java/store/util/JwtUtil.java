package store.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil 
{

    private final String SECRET_KEY = "SomewhereIhaveheardthisbeforeInadreammymemoryhasstoredAsadefenseImneuteredandspayedWhatthehellamItryingtosay";

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // one day (ms cinsinden)

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String mail, String role) {
        return Jwts.builder()
                .setSubject(mail)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractMail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String userMail) {
        final String mail = extractMail(token);
        return (mail.equals(userMail) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
