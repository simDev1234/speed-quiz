package com.example.ranking.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    USER_DISABLED(HttpStatus.FORBIDDEN, "비활성화된 사용자입니다."),
    USER_LOCKED(HttpStatus.LOCKED, "잠긴 사용자 계정입니다."),

    // 인증/인가 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 잘못되었습니다."),
    INVALID_EMAIL_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 인증 코드입니다."),
    EMAIL_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, "이메일 인증 코드가 만료되었습니다."),

    // 입력값 검증 관련 에러
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다."),
    PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "비밀번호가 너무 짧습니다."),

    // 데이터베이스 관련 에러
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
    DATA_INTEGRITY_ERROR(HttpStatus.CONFLICT, "데이터 무결성 오류가 발생했습니다."),
    DUPLICATE_KEY_ERROR(HttpStatus.CONFLICT, "중복된 키 값입니다."),

    // 시스템 관련 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다."),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "요청 시간이 초과되었습니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "너무 많은 요청이 발생했습니다."),

    // 리소스 관련 에러
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),
    RESOURCE_DELETED(HttpStatus.GONE, "삭제된 리소스입니다."),

    // 권한 관련 에러
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "권한이 거부되었습니다."),

    // 퀴즈 관련 에러
    QUESTION_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "문항 데이터를 찾을 수 없습니다."), 
    CHOICE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "보기 데이터를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
