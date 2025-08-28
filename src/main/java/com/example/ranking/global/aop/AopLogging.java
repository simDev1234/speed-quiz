package com.example.ranking.global.aop;

import com.example.ranking.global.util.ClientIpExtractor;
import com.example.ranking.infra.persistence.test.ApiLogEntity;
import com.example.ranking.infra.persistence.test.jpa.ApiLogJpaRepository;
import com.example.ranking.infra.redis.RedisApiLogCache;
import com.example.ranking.infra.redis.RedisApiLogCacheRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AopLogging {

    private final RedisApiLogCacheRepository redisApiLogCacheRepository;
    private final ApiLogJpaRepository apiLogJpaRepository;
    private final ClientIpExtractor clientIpExtractor;

    @Pointcut("execution(* com.example.ranking.interfaces..*RestController.*(..))")
    public void restControllerPointCut(){

    }

    @Around(value = "restControllerPointCut()")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (Objects.isNull(attributes) || Objects.isNull(attributes.getResponse())) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        Object result;

        try {

            result = joinPoint.proceed(); // 실제 컨트롤러 실행

            ApiLogEntity newApiLog = new ApiLogEntity();
            newApiLog.setClientIp(clientIpExtractor.getClientIp(request));
            newApiLog.setRequestMethod(request.getMethod());
            newApiLog.setRequestPath(request.getRequestURI());
            newApiLog.setRequestParam(buildParamsString(request));
            newApiLog.setRequestBody(extractRequestBody(request));
            newApiLog.setResponseStatus(response.getStatus());
            newApiLog.setResponse(extractResponseBody(response));
            newApiLog.setLoggedAt(LocalDateTime.now());

            createOrUpdateApiLogEntity(newApiLog);
            incrementRedisApiLogCacheCount(newApiLog);

        } catch (Throwable e) {
            log.error("Exception during request:: ", e);
            throw e;
        }

        return result;
    }

    private String buildParamsString(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .map(e -> e.getKey() + "=" + Arrays.toString(e.getValue()))
                .collect(Collectors.joining(", "));
    }

    private String extractRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    private String extractResponseBody(HttpServletResponse response) {
        // TODO 이 부분 다른 api로 테스트 필요
        if (response instanceof ContentCachingResponseWrapper wrappedResponse) {
            byte[] buf = wrappedResponse.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, StandardCharsets.UTF_8);
            }
        }
        return "";
    }

    private void createOrUpdateApiLogEntity(ApiLogEntity newApiLog) {

        Optional<ApiLogEntity> optionalApiLogEntity = apiLogJpaRepository.findByClientIpAndRequestMethodAndRequestPath(
                newApiLog.getClientIp(), newApiLog.getRequestMethod(), newApiLog.getRequestPath()
        );

        if (optionalApiLogEntity.isPresent()) {
            ApiLogEntity originalApiLogEntity = optionalApiLogEntity.get();
            newApiLog.setId(originalApiLogEntity.getId());
            newApiLog.setCount(originalApiLogEntity.getCount());
        }

        newApiLog.incrementCount();
        apiLogJpaRepository.save(newApiLog);

    }

    private void incrementRedisApiLogCacheCount(ApiLogEntity newApiLog) {

        String key = RedisApiLogCache.keyFrom(newApiLog.getClientIp(), newApiLog.getRequestMethod(), newApiLog.getRequestPath());
        Optional<RedisApiLogCache> optional = redisApiLogCacheRepository.findByKey(key);

        if (optional.isPresent()) {
            RedisApiLogCache cache = optional.get();
            cache.setCount(newApiLog.getCount());
            redisApiLogCacheRepository.save(cache);
        } else {
            redisApiLogCacheRepository.save(RedisApiLogCache.builder()
                    .key(key)
                    .clientIp(newApiLog.getClientIp())
                    .requestMethod(newApiLog.getRequestMethod())
                    .requestPath(newApiLog.getRequestPath())
                    .count(newApiLog.getCount())
                    .build());
        }
    }


}
