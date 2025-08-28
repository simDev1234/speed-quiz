package com.example.ranking.domain.user.request;

import com.example.ranking.global.validator.Nickname;
import com.example.ranking.global.validator.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public abstract class UserSignUpRequest {

    public record UserEmailAuthRequest(@NotBlank @Email String email) {}

    public record UserEmailVerificationRequest(@NotBlank @Email String email, @NotBlank String verificationCode){}

    public record UserFinalSignUpRequest(@NotBlank @Email String email, @NotBlank @Password String password, @NotBlank @Nickname String nickname){}

}
