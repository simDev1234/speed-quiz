package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ChoicesJpaRepository extends JpaRepository<ChoicesEntity, Long> {

    @Query("select c from ChoicesEntity c " +
            "join fetch c.question " +
            "where c.id = :id")
    Optional<ChoicesEntity> findByIdWithJoinedFetchQuestion(Long id);
}
