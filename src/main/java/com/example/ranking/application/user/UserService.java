package com.example.ranking.application.user;

import com.example.ranking.domain.user.request.UserInfoUpdateRequest;
import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.auth.jwt.JwtTokenProvider;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final UsersJpaRepository usersJpaRepository;

    public void logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        jwtTokenProvider.removeAllAuthTokenCookies(response);
    }

    @Transactional
    public void resetPassword(UserInfoUpdateRequest.UserPasswordReset userPasswordReset) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(userPasswordReset.email())
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        usersEntity.updatePassword(bCryptPasswordEncoder.encode(userPasswordReset.newPassword()));

    }
}
