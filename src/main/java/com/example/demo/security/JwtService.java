package com.example.demo.security;

import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Service
public class JwtService {
    private final ObjectMapper mapper;
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(ObjectMapper mapper, @Value("${app.jwt.secret:}") String configuredSecret,
                      @Value("${app.jwt.expiration-seconds:28800}") long expirationSeconds) {
        this.mapper = mapper;
        this.expirationSeconds = expirationSeconds;
        if (configuredSecret == null || configuredSecret.isBlank()) {
            this.secret = new byte[32]; new SecureRandom().nextBytes(this.secret);
        } else {
            this.secret = MessageDigestHolder.sha256(configuredSecret);
        }
    }

    public String issue(UserAccount user) {
        long now = Instant.now().getEpochSecond();
        String unsigned = encode(Map.of("alg", "HS256", "typ", "JWT")) + "." +
                encode(Map.of("sub", user.getUsername(), "role", user.getRole().name(), "salonId", user.getSalon().getId(), "iat", now, "exp", now + expirationSeconds));
        return unsigned + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(sign(unsigned));
    }

    public Claims verify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Invalid token");
            String unsigned = parts[0] + "." + parts[1];
            if (!MessageDigest.isEqual(sign(unsigned), Base64.getUrlDecoder().decode(parts[2]))) throw new IllegalArgumentException("Invalid signature");
            @SuppressWarnings("unchecked") Map<String,Object> payload = mapper.readValue(Base64.getUrlDecoder().decode(parts[1]), Map.class);
            if (((Number) payload.get("exp")).longValue() <= Instant.now().getEpochSecond()) throw new IllegalArgumentException("Expired token");
            return new Claims((String) payload.get("sub"), (String) payload.get("role"), ((Number) payload.get("salonId")).longValue());
        } catch (Exception exception) { throw new IllegalArgumentException("Invalid or expired token", exception); }
    }

    private String encode(Object value) { try { return Base64.getUrlEncoder().withoutPadding().encodeToString(mapper.writeValueAsBytes(value)); } catch (Exception e) { throw new IllegalStateException(e); } }
    private byte[] sign(String value) { try { Mac mac=Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(secret,"HmacSHA256")); return mac.doFinal(value.getBytes(StandardCharsets.UTF_8)); } catch(Exception e){throw new IllegalStateException(e);} }
    public record Claims(String username, String role, Long salonId) {}
    private static final class MessageDigestHolder { static byte[] sha256(String value) { try { return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)); } catch(Exception e){throw new IllegalStateException(e);} } }
}
