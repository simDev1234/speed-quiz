package com.example.ranking.application.user;

import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import com.example.ranking.domain.user.request.UserSignUpRequest.UserFinalSignUpRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSignUpService {

    private final UsersJpaRepository usersJpaRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    public void signup(UserFinalSignUpRequest userFinalSignUpRequest){

        Optional<UsersEntity> optionalUsersEntity = usersJpaRepository.findUserEntityByEmail(userFinalSignUpRequest.email());

        if (optionalUsersEntity.isPresent()) {
            throw new QuizException(ErrorCode.USER_ALREADY_EXISTS);
        }

        log.info("email : {}, password : {}, nickname : {}",
                userFinalSignUpRequest.email(), userFinalSignUpRequest.password(), userFinalSignUpRequest.nickname());

        usersJpaRepository.save(
                UsersEntity.builder()
                        .email(userFinalSignUpRequest.email())
                        .password(bCryptPasswordEncoder.encode(userFinalSignUpRequest.password()))
                        .nickname(userFinalSignUpRequest.nickname())
                        .build()
        );

    }

}
