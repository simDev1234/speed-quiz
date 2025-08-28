package com.example.ranking.application.quiz;

import com.example.ranking.domain.quiz.response.QuizListResponse.Question;
import com.example.ranking.domain.quiz.response.QuizResultResponse.PersonalQuizResult;
import com.example.ranking.domain.quiz.response.QuizResultResponse.QuizResult;
import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.QuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.quiz.jpa.QuestionsJpaRepository;
import com.example.ranking.infra.persistence.quiz.jpa.QuizAttemptHistoriesJpaRepository;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyQuizReadService {

    private final UsersJpaRepository usersJpaRepository;
    private final QuestionsJpaRepository questionsJpaRepository;
    private final QuizAttemptHistoriesJpaRepository quizAttemptHistoriesJpaRepository;

    public List<Question> findAllActiveQuestions() {

        return questionsJpaRepository.findQuestionsWithChoicesByStatus(QuestionStatus.ACTIVE)
                .stream()
                .map(Question::fromEntity)
                .toList();

    }

    public PersonalQuizResult findUserQuizAttemptHistories(String email) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        List<QuizAttemptHistoriesEntity> latestUserQuizAttemptHistoryList
                = quizAttemptHistoriesJpaRepository.findQuizAttemptHistoriesEntitiesByQuestionStatusAndUser(QuestionStatus.ACTIVE , usersEntity);
        List<QuizResult> questionResults = new ArrayList<>();
        int totalQuestions = 0, correctCounts = 0, totalAttempts = 0;

        for (QuizAttemptHistoriesEntity quizAttemptHistoriesEntity : latestUserQuizAttemptHistoryList) {

            ChoicesEntity selectedChoiceEntity = quizAttemptHistoriesEntity.getSelectedChoice();

            if (Objects.nonNull(selectedChoiceEntity) && selectedChoiceEntity.getIsCorrect()) {
                correctCounts++;
            }
            totalAttempts += quizAttemptHistoriesEntity.getAttemptCount();
            totalQuestions++;

            questionResults.add(QuizResult.fromEntity(quizAttemptHistoriesEntity));

        }

        BigDecimal finalAccuracyRate = BigDecimal.valueOf(correctCounts * 100.0 / (totalQuestions == 0 ? 1 : totalQuestions))
                .setScale(1, RoundingMode.HALF_UP);
        BigDecimal finalScore = BigDecimal.valueOf(correctCounts * 100.0 / totalQuestions)
                .setScale(1, RoundingMode.HALF_UP);

        return PersonalQuizResult.builder()
                .finalScore(finalScore)
                .finalAccuracyRate(finalAccuracyRate)
                .correctAnswerCounts(correctCounts)
                .totalQuestions(totalQuestions)
                .questionResults(questionResults)
                .build();
    }
}
