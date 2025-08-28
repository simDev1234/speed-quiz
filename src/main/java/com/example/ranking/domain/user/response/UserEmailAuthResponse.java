package com.example.ranking.domain.user.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserEmailAuthResponse(String emailAuthVerificationCode, LocalDateTime expirationTime) {
}
