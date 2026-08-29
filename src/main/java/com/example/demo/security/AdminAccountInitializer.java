package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.demo.salon.entity.Salon;
import com.example.demo.salon.repository.SalonRepository;

@Component
public class AdminAccountInitializer implements ApplicationRunner {
    private final UserAccountRepository repository; private final SalonRepository salons; private final PasswordEncoder encoder; private final String username; private final String password;
    public AdminAccountInitializer(UserAccountRepository repository, SalonRepository salons, PasswordEncoder encoder,
            @Value("${ADMIN_USERNAME:}") String username, @Value("${ADMIN_PASSWORD:}") String password) {
        this.repository=repository; this.salons=salons; this.encoder=encoder; this.username=username; this.password=password;
    }
    @Override public void run(ApplicationArguments args) {
        if (!username.isBlank() && !password.isBlank() && repository.findByUsernameIgnoreCase(username).isEmpty()) {
            Salon salon=salons.findById(1L).orElseThrow(() -> new IllegalStateException("Default salon #1 is missing"));
            repository.save(new UserAccount(salon, username, encoder.encode(password), UserRole.ADMIN, null));
        }
    }
}
