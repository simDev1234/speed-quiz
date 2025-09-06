package com.example.ranking.domain.quiz.request;

import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.QuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.user.UsersEntity;

public abstract class QuizSubmitRequest {

    public record UserAnswerChoice(Long questionTitleId, Long questionId, Long selectedChoiceId){
        public static QuizAttemptHistoriesEntity toEntity(
                QuestionsTitlesEntity questionsTitlesEntity,
                UsersEntity usersEntity,
                ChoicesEntity choicesEntity
        ){
            return QuizAttemptHistoriesEntity.builder()
                    .user(usersEntity)
                    .questionTitle(questionsTitlesEntity)
                    .question(choicesEntity.getQuestion())
                    .selectedChoice(choicesEntity)
                    .build();
        }
    }

}
