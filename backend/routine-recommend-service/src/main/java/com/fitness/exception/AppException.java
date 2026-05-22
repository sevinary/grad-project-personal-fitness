package com.fitness.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final int status;
    private final String code;

    public AppException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public static AppException badRequest(String message) {
        return new AppException(400, "INVALID_REQUEST", message);
    }

    public static AppException unauthorized(String message) {
        return new AppException(401, "UNAUTHORIZED", message);
    }

    public static AppException notFound(String message) {
        return new AppException(404, "NOT_FOUND", message);
    }

    public static AppException duplicateUsername() {
        return new AppException(409, "DUPLICATE_USERNAME", "이미 사용 중인 아이디입니다.");
    }

    public static AppException bodyInfoExists() {
        return new AppException(409, "BODY_INFO_EXISTS", "신체 정보가 이미 등록되어 있습니다. PUT을 사용하여 수정하세요.");
    }
}
