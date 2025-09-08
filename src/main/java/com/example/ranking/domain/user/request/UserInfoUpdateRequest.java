package com.example.ranking.domain.user.request;

public abstract class UserInfoUpdateRequest {

    public record UserPasswordReset(String email, String newPassword){};

}
