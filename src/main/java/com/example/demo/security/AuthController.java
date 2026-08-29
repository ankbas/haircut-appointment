package com.example.demo.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAccountRepository repository; private final PasswordEncoder encoder; private final JwtService jwtService;
    public AuthController(UserAccountRepository repository, PasswordEncoder encoder, JwtService jwtService) { this.repository=repository; this.encoder=encoder; this.jwtService=jwtService; }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return repository.findByUsernameIgnoreCase(request.username())
                .filter(UserAccount::isActive).filter(user -> encoder.matches(request.password(), user.getPasswordHash()))
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(new LoginResponse(jwtService.issue(user), user.getUsername(), user.getRole())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthError("Invalid username or password")));
    }
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(String token, String username, UserRole role) {}
    public record AuthError(String detail) {}
}
