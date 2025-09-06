package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface QuestionsJpaRepository extends JpaRepository<QuestionsEntity, Long> {

    @Query("select q from QuestionsEntity q " +
            "join fetch q.subject " +
            "join fetch q.choices " +
            "join fetch q.questionTitle " +
            "where q.questionTitle.id = :questionTitleId " +
            "and q.status = :status " +
            "order by q.id asc")
    List<QuestionsEntity> findQuestionsWithChoicesByQuestionTitleIdAndStatus(@Param("questionTitleId") Long questionTitleId,
                                                                             @Param("status") QuestionStatus status);

//    @Query("select q from QuestionsEntity q " +
//            "join fetch q.subject " +
//            "join fetch q.choices " +
//            "where q.status = :status " +
//            "order by q.id asc")
//    List<QuestionsEntity> findQuestionsWithChoicesByStatus(@Param("status") QuestionStatus status);
}
