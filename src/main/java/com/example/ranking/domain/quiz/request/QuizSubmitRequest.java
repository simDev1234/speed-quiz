package com.example.ranking.domain.quiz.request;

import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.user.UsersEntity;

public abstract class QuizSubmitRequest {

    public record UserAnswerChoice(Long questionId, Long selectedChoiceId){
        public static QuizAttemptHistoriesEntity toEntity(
                UsersEntity usersEntity,
                ChoicesEntity choicesEntity
        ){
            return QuizAttemptHistoriesEntity.builder()
                    .user(usersEntity)
                    .question(choicesEntity.getQuestion())
                    .selectedChoice(choicesEntity)
                    .build();
        }
    }

}
