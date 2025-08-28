package com.example.ranking.global.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class HttpApiResponse<T>{

    @JsonIgnore
    private HttpStatus httpStatus;
    private boolean success;
    private @Nullable T data;
    private @Nullable ErrorResponse exception;

    public static HttpApiResponse<Void> success() {
        return new HttpApiResponse<>(HttpStatus.OK, true, null, null);
    }

    public static <T> HttpApiResponse<T> success(@Nullable final T data) {
        return new HttpApiResponse<>(HttpStatus.OK, true, data, null);
    }

    public static <T> HttpApiResponse<T> fail(final QuizException e) {
        return fail(e.getErrorCode());
    }

    public static <T> HttpApiResponse<T> fail(final ErrorCode errorCode) {
        return new HttpApiResponse<>(errorCode.getHttpStatus(), false, null, ErrorResponse.of(errorCode));
    }

}
