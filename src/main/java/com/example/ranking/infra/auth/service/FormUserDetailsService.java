package com.example.ranking.infra.auth.service;

import com.example.ranking.domain.user.User;
import com.example.ranking.domain.user.UserAuthDetails;
import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class FormUserDetailsService implements UserDetailsService {

    private final UsersJpaRepository usersJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(username)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        return new UserAuthDetails(
                User.builder()
                        .userId(usersEntity.getId())
                        .email(usersEntity.getEmail())
                        .password(usersEntity.getPassword())
                        .nickname(usersEntity.getNickname())
                        .build()
        );
    }
}
