package com.fitness.controller;

import com.fitness.dto.ApiResponse;
import com.fitness.dto.LoginRequest;
import com.fitness.dto.SignupRequest;
import com.fitness.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthService.SignupResult>> signup(
            @Valid @RequestBody SignupRequest request) {
        AuthService.SignupResult result = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthService.LoginResult>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
