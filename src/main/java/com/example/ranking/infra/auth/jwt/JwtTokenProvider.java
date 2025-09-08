package com.example.ranking.infra.auth.jwt;

import com.example.ranking.domain.user.User;
import com.example.ranking.domain.user.UserAuthDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.type}")
    private String authType;

    @Value("${jwt.expire-time.access-token}")
    private Long accessTokenExpirationMilliSeconds;

    public static final String ACCESS_TOKEN_COOKIE_NAME = "jwtAccessToken";

    @PostConstruct
    private void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 쿠키에 토큰 정보를 저장
    public void saveAccessTokenToHttpOnlyCookie(UserDetails userDetails, HttpServletResponse response) {
        if (!(userDetails instanceof UserAuthDetails)) {
            throw new RuntimeException("=====> TODO");
        }
        saveAccessTokenToHttpOnlyCookie(((UserAuthDetails) userDetails).user(), response);
    }

    public void saveAccessTokenToHttpOnlyCookie(User user, HttpServletResponse response){
        saveAccessTokenToHttpOnlyCookie(user.userId(), user.email(), user.nickname(), response);
    }

    public void saveAccessTokenToHttpOnlyCookie(Long userId, String email, String nickname, HttpServletResponse response){

        JwtAccessToken jwtAccessToken = createJwtAccessToken(userId, email,nickname);

        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, jwtAccessToken.content());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (accessTokenExpirationMilliSeconds / 1000)); // seconds

        response.addCookie(cookie);

    }

    // 토큰 생성
    private JwtAccessToken createJwtAccessToken(Long userId, String email, String nickname){

        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + accessTokenExpirationMilliSeconds);

        String accessTokenString = Jwts.builder().addClaims(createClaims(userId, email, nickname))
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return JwtAccessToken.from(authType, accessTokenString, currentDate, expirationDate);
    }

    private Claims createClaims(Long userId, String email, String nickname){

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("nickname", nickname);

        return claims;
    }

    // 토큰 유출 
    public Optional<String> extractJwtAccessTokenStringFromCookie(HttpServletRequest request) {

        try {
            if(Objects.isNull(request) || Objects.isNull(request.getCookies())) {
                return Optional.empty();
            }

            Cookie cookie = Arrays.stream(request.getCookies())
                    .filter(x -> x.getName().equals(ACCESS_TOKEN_COOKIE_NAME))
                    .findFirst()
                    .orElse(null);

            return Objects.nonNull(cookie) ? Optional.of(cookie.getValue()) : Optional.empty();
        } catch (Exception e) {
            log.info("JwtTokenProvider.extractJwtAccessTokenStringFromCookie :: ", e);
            return Optional.empty();
        }

    }
    
    // 토큰 유효성 조회
    public boolean isValidAccessToken(String jwtAccessTokenString){

        try {
            Claims claims  = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtAccessTokenString).getBody();

            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            log.info("JwtTokenProvider -> Token Parsing Error :: ", e);
            return false;
        }

    }

    // 토큰에서 email 조회
    public Optional<String> getEmailFromJwtAccessToken(String jwtAccessTokenString) {

        try {
            Claims claims  = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtAccessTokenString).getBody();

            return Optional.ofNullable(claims.getSubject());
        } catch (Exception e) {
            log.info("JwtTokenProvider -> Token Parsing Error :: ", e);
            return Optional.empty();
        }

    }

    // 토큰 삭제
    public void removeAllAuthTokenCookies(HttpServletResponse response) {
        removeAccessTokenCookie(response);
        removeRememberMeCookie(response);
    }

    private void removeAccessTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 0초 => 즉시 만료
        response.addCookie(cookie);
    }

    private void removeRememberMeCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("rememberMe", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
