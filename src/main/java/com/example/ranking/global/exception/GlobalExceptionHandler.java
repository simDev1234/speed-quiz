package com.example.ranking.global.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        HttpApiResponse<Void> response = HttpApiResponse.fail(ErrorCode.LOGIN_FAILED);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        HttpApiResponse<Void> response = HttpApiResponse.fail(ErrorCode.INVALID_INPUT);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        HttpApiResponse<Void> response = HttpApiResponse.fail(ErrorCode.FORBIDDEN);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<HttpApiResponse<Void>> handleNoSuchElementException(NoSuchElementException ex) {
        HttpApiResponse<Void> response = HttpApiResponse.fail(ErrorCode.RESOURCE_NOT_FOUND);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HttpApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        HttpApiResponse<Void> response = HttpApiResponse.fail(ErrorCode.DATA_INTEGRITY_ERROR);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(QuizException.class)
    public ResponseEntity<HttpApiResponse<Void>> handleQuizException(QuizException ex) {
        HttpApiResponse<Void> response = HttpApiResponse.fail(ex.getErrorCode());
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpApiResponse<Void>> handleGenericException(Exception ex) {
        HttpApiResponse<Void> response = HttpApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}
