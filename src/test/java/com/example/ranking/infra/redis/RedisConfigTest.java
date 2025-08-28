package com.example.ranking.infra.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void testRedisPutAndGet() {
        redisTemplate.opsForValue().set("testKey", "testValue");
        String result = redisTemplate.opsForValue().get("testKey");
        assertEquals("testValue", result);
        System.out.println("============Success==========");
    }

}