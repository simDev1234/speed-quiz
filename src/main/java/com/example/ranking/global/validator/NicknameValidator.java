package com.example.ranking.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {

        if (nickname == null) return false;

        return !nickname.contains(" ");
    }
}
