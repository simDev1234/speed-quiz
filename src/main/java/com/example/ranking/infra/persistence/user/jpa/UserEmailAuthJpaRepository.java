package com.example.ranking.infra.persistence.user.jpa;

import com.example.ranking.infra.persistence.user.UserEmailAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserEmailAuthJpaRepository extends JpaRepository<UserEmailAuthEntity, Long> {

    Optional<UserEmailAuthEntity> findByCodeAndExpiratedAtAfter(String code, LocalDateTime currentTime);
    Optional<UserEmailAuthEntity> findByCode(String code);

}
