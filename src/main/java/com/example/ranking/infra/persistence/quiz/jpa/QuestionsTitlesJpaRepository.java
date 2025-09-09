package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface QuestionsTitlesJpaRepository extends JpaRepository<QuestionsTitlesEntity, Long>, QuestionTitleQueryDslRepository {

//    @Query( "SELECT qt " +
//            "FROM QuestionsTitlesEntity qt " +
//            "JOIN FETCH qt.subjectsEntity s " +
//            "WHERE qt.status = :status")
//    List<QuestionsTitlesEntity> findAllByStatusWithJoinFetch(@Param("status") QuestionTitleStatus status);

    List<QuestionsTitlesEntity> findAllByStatusAndUser(QuestionTitleStatus status, UsersEntity user);

    @Query( "SELECT qt FROM QuestionsTitlesEntity qt " +
            "JOIN FETCH qt.user u " +
            "JOIN FETCH qt.questions q " +
            "WHERE qt.id =  :questionTitleId " +
            "AND qt.user = :user " +
            "AND q.status = 'ACTIVE'")
    Optional<QuestionsTitlesEntity> findByIdAndUserAndStatusWithJoinFetch(@Param("questionTitleId") Long questionTitleId,
                                                                          @Param("user") UsersEntity usersEntity);
}
