package com.example.ranking.infra.persistence.test.jpa;

import com.example.ranking.infra.persistence.test.ApiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiLogJpaRepository extends JpaRepository<ApiLogEntity, Long> {

    Optional<ApiLogEntity> findByClientIpAndRequestMethodAndRequestPath(String clientId, String requestMethod, String requestPath);

}
