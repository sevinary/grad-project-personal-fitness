package com.fitness.service;

import com.fitness.dto.LoginRequest;
import com.fitness.dto.SignupRequest;
import com.fitness.exception.AppException;
import com.fitness.model.User;
import com.fitness.repository.UserRepository;
import com.fitness.security.JwtUtil;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public SignupResult signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw AppException.duplicateUsername();
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        user = userRepository.save(user);

        return new SignupResult(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedDate()
        );
    }

    public LoginResult login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> AppException.unauthorized("아이디 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw AppException.unauthorized("아이디 또는 비밀번호가 잘못되었습니다.");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId());

        return new LoginResult(token, "Bearer", user.getUserId(), user.getUsername());
    }

    @Getter
    public static class SignupResult {
        private final Long userId;
        private final String username;
        private final String email;
        private final LocalDateTime createdAt;

        public SignupResult(Long userId, String username, String email, LocalDateTime createdAt) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.createdAt = createdAt;
        }
    }

    @Getter
    public static class LoginResult {
        private final String accessToken;
        private final String tokenType;
        private final Long userId;
        private final String username;

        public LoginResult(String accessToken, String tokenType, Long userId, String username) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.userId = userId;
            this.username = username;
        }
    }
}
