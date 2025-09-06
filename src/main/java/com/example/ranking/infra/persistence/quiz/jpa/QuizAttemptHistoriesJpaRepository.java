package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.QuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptHistoriesJpaRepository extends JpaRepository<QuizAttemptHistoriesEntity, Long> {

    Optional<QuizAttemptHistoriesEntity> findQuizAttemptHistoriesEntitiesByQuestionAndUser(QuestionsEntity questionsEntity, UsersEntity usersEntity);

    @Query( "select qah " +
            "from QuizAttemptHistoriesEntity qah " +
            "join qah.questionTitle " +
            "join fetch qah.question q " +
            "left join fetch qah.selectedChoice sc " +
            "join fetch qah.correctChoice cc " +
            "where qah.question.status = :questionStatus " +
            "and qah.user = :user " +
            "and qah.questionTitle = :questionTitle")
    List<QuizAttemptHistoriesEntity> findQuizAttemptHistoriesEntitiesByQuestionStatusAndUserAndQuestionTitle(
            @Param("questionStatus") QuestionStatus questionStatus,
            @Param("user") UsersEntity usersEntity,
            @Param("questionTitle")QuestionsTitlesEntity questionsTitlesEntity
    );

}
