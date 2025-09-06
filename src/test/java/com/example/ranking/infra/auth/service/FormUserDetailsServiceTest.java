package com.example.ranking.infra.auth.service;

import com.example.ranking.domain.user.UserAuthDetails;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormUserDetailsServiceTest {

    @InjectMocks
    private FormUserDetailsService formUserDetailsService;

    @Mock
    private UsersJpaRepository usersJpaRepository;

    @Test
    void loadUserByUsername_should_return_UserAuthDetails_when_user_exists() {
        // given
        Long userId = 1L;
        String email = "test@test.com";
        String encodedPassword = "$2a$10$someEncodedPassword";
        String nickname = "testNickname";
        UsersEntity usersEntity = UsersEntity.builder()
                .id(userId)
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();

        when(usersJpaRepository.findUserEntityByEmail(email)).thenReturn(Optional.of(usersEntity));

        // when
        UserDetails userDetails = formUserDetailsService.loadUserByUsername(email);

        // then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(encodedPassword, userDetails.getPassword());
        assertInstanceOf(UserAuthDetails.class, userDetails);
        //assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_should_throw_exception_when_user_not_found() {
        // given
        String email = "notfound@example.com";
        when(usersJpaRepository.findUserEntityByEmail(email)).thenReturn(Optional.empty());

        // expect
        assertThrows(UsernameNotFoundException.class, () -> {
            formUserDetailsService.loadUserByUsername(email);
        });
    }
}