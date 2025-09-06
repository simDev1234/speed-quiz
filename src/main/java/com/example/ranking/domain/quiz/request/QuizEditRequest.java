package com.example.ranking.domain.quiz.request;

import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.SubjectsEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;

import java.util.List;

public abstract class QuizEditRequest {

    public record QuizEdit(
            Long questionTitleId,
            String titleText,
            String description,
            Long subjectId,
            Integer timeLimit,
            List<Question> questions
    ){

        public static QuestionsTitlesEntity toQuestionsTitlesEntity(SubjectsEntity subjectsEntity, UsersEntity usersEntity, QuizEdit quizEdit){
            return QuestionsTitlesEntity.builder()
                    .user(usersEntity)
                    .subjectsEntity(subjectsEntity)
                    .title(quizEdit.titleText)
                    .description(quizEdit.description)
                    .timeLimit(quizEdit.timeLimit())
                    .status(QuestionTitleStatus.ACTIVE)
                    .build();
        }
    }

    public record Question(
            Long questionId,
            String questionText,
            List<Choice> choices,
            Long correctAnswerIndex
    ){
    }

    public record Choice(
            Long choiceId,
            String choiceText,
            Boolean isCorrect
    ){

    }

}
