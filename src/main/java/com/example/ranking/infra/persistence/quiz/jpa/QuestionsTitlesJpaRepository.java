package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuestionsTitlesJpaRepository extends JpaRepository<QuestionsTitlesEntity, Long> {

    List<QuestionsTitlesEntity> findAllByStatus(QuestionTitleStatus status);

    // TODO
    Optional<QuestionsTitlesEntity> findByIdAndUser(Long id, UsersEntity user);


}
