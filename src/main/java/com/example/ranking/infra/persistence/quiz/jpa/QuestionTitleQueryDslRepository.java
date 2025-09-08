package com.example.ranking.infra.persistence.quiz.jpa;

import com.example.ranking.domain.quiz.response.QuizListResponse.QuestionTitle;
import com.example.ranking.infra.persistence.user.UsersEntity;

import java.util.List;

public interface QuestionTitleQueryDslRepository {
    List<QuestionTitle> findAllActiveQuestionTitlesWithParticipationHistory(UsersEntity currentLoggedInUser);
}
