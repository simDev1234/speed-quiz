package com.example.ranking.infra.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "redisApiLogCache")
@AllArgsConstructor
@Setter
@Getter
@Builder
public class RedisApiLogCache {

    @Id
    private final String key;
    private String clientIp;
    private String requestPath;
    private String requestMethod;
    private Long count;

    @TimeToLive
    private Long ttl = 10L;

    public static String keyFrom(String clientIp, String method, String path) {
        return String.format("%s::%s::%s", clientIp, method, path);
    }

}
