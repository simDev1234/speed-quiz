package com.example.ranking.infra.auth.jwt;

import lombok.Builder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Builder
public record JwtAccessToken(String authType, String content, LocalDateTime issuedLocalDateTime, LocalDateTime expirationLocalDateTime) {
    public static JwtAccessToken from(String authType, String content, Date issuedDate, Date expirationDate){
        return JwtAccessToken.builder()
                .authType(authType)
                .content(content)
                .issuedLocalDateTime(LocalDateTime.ofInstant(issuedDate.toInstant(), ZoneId.of("Asia/Seoul")))
                .expirationLocalDateTime(LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.of("Asia/Seoul")))
                .build();
    }
}
