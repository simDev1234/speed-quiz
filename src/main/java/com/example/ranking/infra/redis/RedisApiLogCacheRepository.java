package com.example.ranking.infra.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisApiLogCacheRepository extends CrudRepository<RedisApiLogCache, String> {

    Optional<RedisApiLogCache> findByKey(String key);

}
