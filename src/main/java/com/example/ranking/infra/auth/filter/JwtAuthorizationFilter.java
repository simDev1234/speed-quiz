package com.example.ranking.infra.auth.filter;

import com.example.ranking.infra.auth.service.FormUserDetailsService;
import com.example.ranking.infra.auth.StaticResourceLocation;
import com.example.ranking.infra.auth.jwt.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final FormUserDetailsService formUserDetailsService;
    private final RememberMeServices rememberMeServices;
    private static final String[] permittedPaths = {
            "/login",
            "/api/v1/users/login",
            "/api/v1/users/email/auth",
            "/api/v1/users/email/code",
            "/api/v1/users/signup",
            "/api/v1/users/password-reset/*"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String currentPath = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.info("OPTIONS method detected. Skip authorization filter.");
            return true;
        }

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String authPath : permittedPaths) {
            if (antPathMatcher.match(authPath, currentPath) || antPathMatcher.match(authPath + "/*", currentPath)) {
                log.info("Permitted path detected. Skip authorization filter.");
                return true;
            }
        }

        for (String resourcesPath : StaticResourceLocation.getAllStaticResourcePaths()) {
            if (antPathMatcher.match(resourcesPath, currentPath)) {
                log.info("Static resource detected. Skip authorization filter.");
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

          try {

              Optional<String> optionalJwtTokenString = jwtTokenProvider.extractJwtAccessTokenStringFromCookie(request);

              log.info("optionalJwtTokenStringExtraction : Success");

              if (optionalJwtTokenString.isPresent() && jwtTokenProvider.isValidAccessToken(optionalJwtTokenString.get())) {

                  String email = jwtTokenProvider.getEmailFromJwtAccessToken(optionalJwtTokenString.get())
                          .orElseThrow(() -> new JwtException("Email not found from Jwt Token"));

                  UserDetails userDetails = formUserDetailsService.loadUserByUsername(email);
                  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                  SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

              }

              if (SecurityContextHolder.getContext().getAuthentication() == null && !shouldNotFilter(request)) {
                  Authentication rememberMeAuth = rememberMeServices.autoLogin(request, response);
                  if (rememberMeAuth != null) {
                      SecurityContextHolder.getContext().setAuthentication(rememberMeAuth);
                      log.info("Remember-Me 인증 성공");
                  }
              }

              log.info("shouldNotFilter? {} -> {}", request.getRequestURI(), shouldNotFilter(request));

              filterChain.doFilter(request, response);

          } catch (JwtException e) {
              log.error("JwtAuthorizationFilter -> JwtException :: {}", e.getMessage(), e);
              SecurityContextHolder.clearContext();
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              response.setContentType("application/json");
              response.getWriter().write("{\"error\": \"Invalid or expired JWT token\"}");
              return; // 응답 전송 후 체인 중단
          } catch (Exception e) {
              log.error("JwtAuthorizationFilter -> Exception :: {}", e.getMessage(), e);
              SecurityContextHolder.clearContext();
              response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              response.setContentType("application/json");
              response.getWriter().write("{\"error\": \"Internal server error\"}");
              return; // 응답 전송 후 체인 중단
          }

    }

}
