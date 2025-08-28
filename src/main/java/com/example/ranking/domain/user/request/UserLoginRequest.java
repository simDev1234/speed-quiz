package com.example.ranking.domain.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
        @Email @NotBlank String loginEmail,
        @NotBlank String loginPassword,
        @NotNull Boolean rememberMe
) {

}
