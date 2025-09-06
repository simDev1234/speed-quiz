package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionsTitlesJpaRepository extends JpaRepository<QuestionsTitlesEntity, Long> {

    List<QuestionsTitlesEntity> findAllByStatus(QuestionTitleStatus status);

    List<QuestionsTitlesEntity> findAllByStatusAndUser(QuestionTitleStatus status, UsersEntity user);

    @Query( "SELECT qt FROM QuestionsTitlesEntity qt " +
            "JOIN FETCH qt.user u " +
            "JOIN FETCH qt.questions q " +
            "WHERE qt.id =  :questionTitleId " +
            "AND qt.user = :user")
    Optional<QuestionsTitlesEntity> findByIdAndUserWithJoinFetch(@Param("questionTitleId") Long questionTitleId,
                                                                 @Param("user") UsersEntity usersEntity);
}
