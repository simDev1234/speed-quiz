package com.example.ranking.interfaces.user;

import com.example.ranking.application.user.UserLoginService;
import com.example.ranking.application.user.UserService;
import com.example.ranking.application.user.UserSignUpService;
import com.example.ranking.global.exception.HttpApiResponse;
import com.example.ranking.infra.auth.service.UserMailAuthService;
import com.example.ranking.domain.user.request.UserLoginRequest;
import com.example.ranking.domain.user.request.UserSignUpRequest.UserEmailAuthRequest;
import com.example.ranking.domain.user.request.UserSignUpRequest.UserEmailVerificationRequest;
import com.example.ranking.domain.user.request.UserSignUpRequest.UserFinalSignUpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {

    private final UserMailAuthService userMailAuthService;
    private final UserLoginService userLoginService;
    private final UserSignUpService userSignUpService;
    private final UserService userService;

    @PostMapping("/login")
    public HttpApiResponse<Void> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        userLoginService.login(userLoginRequest, request, response);

        return HttpApiResponse.success();
    }

    @PostMapping("/email/auth")
    public HttpApiResponse<Void> sendEmailAuth(@RequestBody UserEmailAuthRequest userEmailAuthRequest) {

        userMailAuthService.sendEmailAuthVerificationCode(userEmailAuthRequest.email());

        return HttpApiResponse.success();
    }

    @PostMapping("/email/code")
    public HttpApiResponse<Void> verifyEmailCode(@RequestBody UserEmailVerificationRequest userEmailVerificationRequest) {

        userMailAuthService.verifyEmailAuthCode(userEmailVerificationRequest.verificationCode());

        return HttpApiResponse.success();
    }

    @PostMapping("/signup")
    public HttpApiResponse<Void> signup(@RequestBody UserFinalSignUpRequest userFinalSignUpRequest) {

        userSignUpService.signup(userFinalSignUpRequest);

        return HttpApiResponse.success();
    }

    @DeleteMapping("/logout")
    public HttpApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        userService.logout(request, response);

        return HttpApiResponse.success();
    }

}
