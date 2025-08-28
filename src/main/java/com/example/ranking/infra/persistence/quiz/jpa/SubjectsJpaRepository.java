package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.SubjectsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectsJpaRepository extends JpaRepository<SubjectsEntity, Long> {
}
