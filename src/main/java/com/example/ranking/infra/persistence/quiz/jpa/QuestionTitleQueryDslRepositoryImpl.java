package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.domain.quiz.response.QuizListResponse;
import com.example.ranking.domain.quiz.response.QuizListResponse.QuestionTitle;
import com.example.ranking.infra.persistence.quiz.QQuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.QQuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.QUsersEntity;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionTitleQueryDslRepositoryImpl implements QuestionTitleQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QuestionTitle> findAllActiveQuestionTitlesWithParticipationHistory(UsersEntity currentLoggedInUser) {
        QQuestionsTitlesEntity qt = QQuestionsTitlesEntity.questionsTitlesEntity;
        QQuizAttemptHistoriesEntity qah = QQuizAttemptHistoriesEntity.quizAttemptHistoriesEntity;
        QUsersEntity user = QUsersEntity.usersEntity;

        return queryFactory
                .select(Projections.constructor(
                        QuizListResponse.QuestionTitle.class,
                        qt.id,
                        user.id,
                        qt.title,
                        qt.description,
                        qt.timeLimit,
                        qt.createdAt,
                        qt.updatedAt,
                        qah.count().gt(0)
                ))
                .from(qt)
                .leftJoin(qt.user, user)
                .leftJoin(qt.quizAttemptHistories, qah)
                .on(qt.user.eq(qah.user).and(qah.user.eq(currentLoggedInUser)))
                .where(qt.status.eq(QuestionTitleStatus.ACTIVE))
                .groupBy(qt.id, user.id, qt.title, qt.description, qt.timeLimit, qt.createdAt, qt.updatedAt)
                .fetch();
    }
}
