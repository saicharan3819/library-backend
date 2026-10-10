package com.library.service;

import com.library.config.TokenStore;
import com.library.dto.Dtos.*;
import com.library.exception.ApiException;
import com.library.model.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final StudentService students;
    private final PasswordEncoder encoder;
    private final TokenStore tokens;

    public AuthResponse register(RegisterRequest r) {
        User u = students.create(r);                       // public sign-up always creates a STUDENT
        return new AuthResponse(tokens.create(u.getId()), u);
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase())
                .orElseThrow(() -> new ApiException(401, "Invalid email or password"));
        if (!encoder.matches(r.password(), u.getPassword()))
            throw new ApiException(401, "Invalid email or password");
        return new AuthResponse(tokens.create(u.getId()), u);
    }
}
