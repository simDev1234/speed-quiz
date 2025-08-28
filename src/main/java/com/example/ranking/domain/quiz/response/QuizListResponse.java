package com.example.ranking.domain.quiz.response;

import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.SubjectsEntity;
import lombok.Builder;
import java.util.List;

public abstract class QuizListResponse {

    @Builder
    public record Subject(
            Long subjectId,
            String subjectName
    ){
        public static Subject fromEntity(SubjectsEntity subjectsEntity){
            return Subject.builder()
                    .subjectId(subjectsEntity.getId())
                    .subjectName(subjectsEntity.getName())
                    .build();
        }
    }

    @Builder
    public record Question(
            Long questionId,
            Subject subject,
            String questionType,
            String questionText,
            List<Choice> choices
    ) {
        public static Question fromEntity(QuestionsEntity questionsEntity){
            return Question.builder()
                    .questionId(questionsEntity.getId())
                    .subject(Subject.fromEntity(questionsEntity.getSubject()))
                    .questionType(questionsEntity.getQuestionType().name())
                    .questionText(questionsEntity.getQuestionText())
                    .choices(questionsEntity.getChoices().stream().map(Choice::fromEntity).toList())
                    .build();
        }
    }

    @Builder
    public record Choice(
            Long choiceId,
            String choiceText,
            boolean isCorrect
    ){
        public static Choice fromEntity(ChoicesEntity choicesEntity){
            return Choice.builder()
                    .choiceId(choicesEntity.getId())
                    .choiceText(choicesEntity.getText())
                    .isCorrect(choicesEntity.getIsCorrect())
                    .build();
        }
    }
}
