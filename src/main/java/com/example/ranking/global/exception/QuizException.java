package com.example.ranking.global.exception;

import lombok.Getter;

@Getter
public class QuizException extends RuntimeException{

    ErrorCode errorCode;

    public QuizException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public QuizException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public QuizException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public QuizException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public QuizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
