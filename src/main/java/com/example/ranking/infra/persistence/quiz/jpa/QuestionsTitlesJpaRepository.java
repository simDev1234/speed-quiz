package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionsTitlesJpaRepository extends JpaRepository<QuestionsTitlesEntity, Long> {

    List<QuestionsTitlesEntity> findAllByStatus(QuestionTitleStatus status);

    List<QuestionsTitlesEntity> findAllByStatusAndUser(QuestionTitleStatus status, UsersEntity user);

}
