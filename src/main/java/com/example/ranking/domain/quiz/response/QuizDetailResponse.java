package com.example.ranking.domain.quiz.response;

import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public abstract class QuizDetailResponse {

    @Builder
    public record QuizDetail(
            Long subjectId,
            Long questionTitleId,
            String titleText,
            String description,
            Integer timeLimit,
            List<QuizListResponse.Question> questions,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ){
        public static QuizDetail fromEntity(QuestionsTitlesEntity questionsTitlesEntity){
            return QuizDetail.builder()
                    .subjectId(questionsTitlesEntity.getSubjectsEntity().getId())
                    .questionTitleId(questionsTitlesEntity.getId())
                    .titleText(questionsTitlesEntity.getTitle())
                    .description(questionsTitlesEntity.getDescription())
                    .timeLimit(questionsTitlesEntity.getTimeLimit())
                    .questions(questionsTitlesEntity.getQuestions().stream().map(QuizListResponse.Question::fromEntity).toList())
                    .createdAt(questionsTitlesEntity.getCreatedAt())
                    .updatedAt(questionsTitlesEntity.getUpdatedAt())
                    .build();
        }
    }
}
