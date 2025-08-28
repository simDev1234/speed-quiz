package com.example.ranking.infra.persistence.user.jpa;

import com.example.ranking.infra.persistence.user.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersJpaRepository extends JpaRepository<UsersEntity, Long> {

    Optional<UsersEntity> findUserEntityByEmail(String email);

}
