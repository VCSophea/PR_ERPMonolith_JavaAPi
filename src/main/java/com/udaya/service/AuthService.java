package com.udaya.service;

import com.udaya.dto.LoginRequest;
import com.udaya.dto.LoginResponse;
import com.udaya.exception.GlobalException;
import com.udaya.model.User;
import com.udaya.repository.UserRepository;
import com.udaya.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // * Authenticate User
    public LoginResponse login(LoginRequest request) {
        // * TODO: Use PasswordEncoder in real app
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new GlobalException("Invalid credentials", 401));

        if (!user.getPassword().equals(request.getPassword())) { // Simple check
            throw new GlobalException("Invalid credentials", 401);
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }
}
