package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.domain.quiz.response.QuizListResponse;
import com.example.ranking.domain.quiz.response.QuizListResponse.QuestionTitle;
import com.example.ranking.infra.persistence.quiz.QQuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.QQuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.quiz.QSubjectsEntity;
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
    public List<QuestionTitle> findAllQuestionTitlesWithParticipationCountOrderByParticipationCountDesc() {

        QSubjectsEntity subject = QSubjectsEntity.subjectsEntity;
        QQuestionsTitlesEntity qt = QQuestionsTitlesEntity.questionsTitlesEntity;
        QQuizAttemptHistoriesEntity qah = QQuizAttemptHistoriesEntity.quizAttemptHistoriesEntity;
        QUsersEntity user = QUsersEntity.usersEntity;

        return queryFactory
                .select(Projections.constructor(
                        QuizListResponse.QuestionTitle.class,
                            qt.subjectsEntity.id,
                            qt.id,
                            qt.user.id,
                            qt.title,
                            qt.description,
                            qt.timeLimit,
                            qt.createdAt,
                            qt.updatedAt,
                            qah.count()
                    ))
                .from(qt)
                .join(qt.subjectsEntity)
                .join(qt.user)
                .leftJoin(qt.quizAttemptHistories, qah)
                .where(qt.status.eq(QuestionTitleStatus.ACTIVE))
                .groupBy(qt.id)
                .orderBy(qah.count().desc())
                .fetch();
    }
}
