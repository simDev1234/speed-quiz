package com.example.ranking.application.user;

import com.example.ranking.domain.user.User;
import com.example.ranking.domain.user.UserAuthDetails;
import com.example.ranking.domain.user.request.UserLoginRequest;
import com.example.ranking.infra.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    @InjectMocks
    private UserLoginService userLoginService;

    @Mock
    private UserDetailsService formUserDetailsService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private RememberMeServices rememberMeServices;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void login() {

        // given
        Long userId = 1L;
        String email = "test@test.com";
        String rawPassword = "test1234!Q";
        String encodedPassword = "$2a$10$someEncodedPassword";
        String nickname = "testNickname";
        boolean rememberMe = false;

        UserLoginRequest userLoginRequest = new UserLoginRequest(email, rawPassword, rememberMe);
        UserAuthDetails userDetails = new UserAuthDetails(new User(
                userId, email, encodedPassword, nickname
        ));

        when(formUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(bCryptPasswordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // when
        userLoginService.login(userLoginRequest, request, response);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals(userDetails.getUsername(), ((UserDetails) authentication.getPrincipal()).getUsername());

        verify(jwtTokenProvider, times(1)).saveAccessTokenToHttpOnlyCookie(userDetails, response);
        verify(rememberMeServices, never()).loginSuccess(any(), any(), any());

    }
}