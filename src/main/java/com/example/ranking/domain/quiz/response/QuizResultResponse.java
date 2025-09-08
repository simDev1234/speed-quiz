package com.example.ranking.domain.quiz.response;

import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.QuizAttemptHistoriesEntity;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public abstract class QuizResultResponse {

    @Builder
    public record PersonalQuizResult(
        Long questionTitleId,
        BigDecimal finalScore,
        BigDecimal finalAccuracyRate,
        int correctAnswerCounts,
        int totalQuestions,
        List<QuizResult> questionResults
    ){

    }

    @Builder
    public record QuizResult(
        String subjectName,
        Long questionId,
        String questionText,
        String userAnswer,
        String correctAnswer,
        boolean isCorrect
    ) {

        public static QuizResult fromEntity(QuizAttemptHistoriesEntity quizAttemptHistoriesEntity) {
            QuestionsEntity questionsEntity = quizAttemptHistoriesEntity.getQuestion();
            ChoicesEntity selectedChoiceEntity = quizAttemptHistoriesEntity.getSelectedChoice();
            ChoicesEntity correctChoiceEntity = quizAttemptHistoriesEntity.getCorrectChoice();
            return QuizResult.builder()
                    .questionText(questionsEntity.getQuestionText())
                    .subjectName(questionsEntity.getSubject().getName())
                    .userAnswer(Objects.nonNull(selectedChoiceEntity) ? selectedChoiceEntity.getText() : null)
                    .correctAnswer(correctChoiceEntity.getText())
                    .isCorrect(Objects.nonNull(selectedChoiceEntity) && selectedChoiceEntity.getIsCorrect())
                    .build();
        }

    }

}
