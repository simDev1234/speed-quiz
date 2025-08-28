package com.example.ranking.domain.user;

import lombok.Builder;

@Builder
public record User(Long userId, String email, String password, String nickname) {
}
