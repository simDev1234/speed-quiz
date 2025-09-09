package com.example.ranking.domain.quiz.response;

import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.SubjectsEntity;
import lombok.Builder;

import java.time.LocalDateTime;
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
    public record QuestionTitle(
            Long subjectId,
            Long questionTitleId,
            Long userId,
            String titleText,
            String description,
            Integer timeLimit,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            long participationCount
    ){

        public static QuestionTitle fromEntity(QuestionsTitlesEntity questionsTitlesEntity, long participationCount){
            return QuestionTitle.builder()
                    .subjectId(questionsTitlesEntity.getSubjectsEntity().getId())
                    .questionTitleId(questionsTitlesEntity.getId())
                    .userId(questionsTitlesEntity.getUser().getId())
                    .titleText(questionsTitlesEntity.getTitle())
                    .description(questionsTitlesEntity.getDescription())
                    .timeLimit(questionsTitlesEntity.getTimeLimit())
                    .createdAt(questionsTitlesEntity.getCreatedAt())
                    .updatedAt(questionsTitlesEntity.getUpdatedAt())
                    .participationCount(participationCount)
                    .build();
        }

        public static QuestionTitle fromEntity(QuestionsTitlesEntity questionsTitlesEntity){
            return QuestionTitle.builder()
                    .subjectId(questionsTitlesEntity.getSubjectsEntity().getId())
                    .questionTitleId(questionsTitlesEntity.getId())
                    .userId(questionsTitlesEntity.getUser().getId())
                    .titleText(questionsTitlesEntity.getTitle())
                    .description(questionsTitlesEntity.getDescription())
                    .timeLimit(questionsTitlesEntity.getTimeLimit())
                    .createdAt(questionsTitlesEntity.getCreatedAt())
                    .updatedAt(questionsTitlesEntity.getUpdatedAt())
                    .build();
        }

//        public QuestionTitle(Long questionTitleId, Long userId, String titleText,
//                             String description, Integer timeLimit, LocalDateTime createdAt,
//                             LocalDateTime updatedAt, Boolean hasParticipationHistory) {
//            this.questionTitleId = questionTitleId;
//            this.userId = userId;
//            this.titleText = titleText;
//            this.description = description;
//            this.timeLimit = timeLimit;
//            this.createdAt = createdAt;
//            this.updatedAt = updatedAt;
//            this.hasParticipationHistory = hasParticipationHistory;
//        }

    }

    @Builder
    public record Question(
            Long questionTitleId,
            Long questionId,
            Subject subject,
            String questionType,
            String questionText,
            Integer timeLimit,
            List<Choice> choices
    ) {
        public static Question fromEntity(QuestionsEntity questionsEntity){
            return Question.builder()
                    .questionTitleId(questionsEntity.getQuestionTitle().getId())
                    .questionId(questionsEntity.getId())
                    .subject(Subject.fromEntity(questionsEntity.getSubject()))
                    .questionType(questionsEntity.getQuestionType().name())
                    .questionText(questionsEntity.getQuestionText())
                    .timeLimit(questionsEntity.getQuestionTitle().getTimeLimit())
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
