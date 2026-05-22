package com.fitness.controller;

import com.fitness.dto.ApiResponse;
import com.fitness.dto.BodyInfoRequest;
import com.fitness.service.BodyInfoApiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/body-info")
public class BodyInfoController {

    private final BodyInfoApiService bodyInfoApiService;

    public BodyInfoController(BodyInfoApiService bodyInfoApiService) {
        this.bodyInfoApiService = bodyInfoApiService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BodyInfoApiService.BodyInfoResult>> saveBodyInfo(
            @Valid @RequestBody BodyInfoRequest request) {
        Long userId = getCurrentUserId();
        BodyInfoApiService.BodyInfoResult result = bodyInfoApiService.save(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result));
    }

    private Long getCurrentUserId() {
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getDetails();
    }
}
