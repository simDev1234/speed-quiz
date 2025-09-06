package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.SubjectsEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.SubjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectsJpaRepository extends JpaRepository<SubjectsEntity, Long> {

    List<SubjectsEntity> findAllByStatus(SubjectStatus status);

}
