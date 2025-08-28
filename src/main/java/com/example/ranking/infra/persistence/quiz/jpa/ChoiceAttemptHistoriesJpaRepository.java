package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.ChoiceAttemptHistoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceAttemptHistoriesJpaRepository extends JpaRepository<ChoiceAttemptHistoriesEntity, Long> {
}
