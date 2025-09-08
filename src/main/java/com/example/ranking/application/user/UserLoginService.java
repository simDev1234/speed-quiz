package com.example.ranking.application.user;

import com.example.ranking.domain.user.request.UserLoginRequest;
import com.example.ranking.infra.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {

    private final UserDetailsService formUserDetailsService;
    private final RememberMeServices rememberMeServices;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void login(UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response){

        UserDetails userDetails = formUserDetailsService.loadUserByUsername(userLoginRequest.loginEmail());

        log.info("Login User: {}", userDetails.getUsername());

        if (!bCryptPasswordEncoder.matches(userLoginRequest.loginPassword(), userDetails.getPassword())) {
            log.warn("❌ 로그인 실패 - 이메일: {}", userLoginRequest.loginEmail());
            throw new BadCredentialsException("비밀번호가 잘못되었습니다.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            log.info("====> Remember Me에 의해 이미 로그인됨");
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        if (userLoginRequest.rememberMe()) {
            log.info("Remember Me 설정 - 사용자: {}", userDetails.getUsername());
            rememberMeServices.loginSuccess(request, response, usernamePasswordAuthenticationToken);
        }

        jwtTokenProvider.saveAccessTokenToHttpOnlyCookie(userDetails, response);

        log.info("✅ 로그인 성공 - 사용자: {}", userDetails.getUsername());

    }

}
