package com.example.ranking.application.quiz;

import com.example.ranking.domain.quiz.request.QuizSubmitRequest.UserAnswerChoice;
import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.QuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.quiz.jpa.ChoicesJpaRepository;
import com.example.ranking.infra.persistence.quiz.jpa.QuestionsJpaRepository;
import com.example.ranking.infra.persistence.quiz.jpa.QuizAttemptHistoriesJpaRepository;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyQuizWriteService {

    private final UsersJpaRepository usersJpaRepository;
    private final QuestionsJpaRepository questionsJpaRepository;
    private final ChoicesJpaRepository choicesJpaRepository;
    private final QuizAttemptHistoriesJpaRepository quizAttemptHistoriesJpaRepository;

    public void saveQuizAnswers(String email, List<UserAnswerChoice> userAnswerChoices) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        List<QuizAttemptHistoriesEntity> bufferedQuizAttemptHistoriesList = new ArrayList<>();

        for (UserAnswerChoice userAnswerChoice : userAnswerChoices) {

            QuestionsEntity questionsEntity = questionsJpaRepository.findById(userAnswerChoice.questionId())
                    .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_NOT_FOUND));

            ChoicesEntity correctChoiceEntity = questionsEntity.getChoices().stream()
                    .filter(ChoicesEntity::getIsCorrect).findFirst().orElseThrow(() -> new QuizException(ErrorCode.CHOICE_NOT_FOUND));

            ChoicesEntity selectedChoiceEntity = Objects.isNull(userAnswerChoice.selectedChoiceId()) ? null :
                    choicesJpaRepository.findByIdWithJoinedFetchQuestion(userAnswerChoice.selectedChoiceId())
                            .orElseThrow(() -> new QuizException(ErrorCode.CHOICE_NOT_FOUND));

            QuizAttemptHistoriesEntity quizAttemptHistoriesEntity = createOrUpdateQuizAttemptHistoriesEntity(questionsEntity, usersEntity, selectedChoiceEntity, correctChoiceEntity);
            quizAttemptHistoriesEntity.incrementAttemptCount();
            bufferedQuizAttemptHistoriesList.add(quizAttemptHistoriesEntity);

        }

        quizAttemptHistoriesJpaRepository.saveAll(bufferedQuizAttemptHistoriesList);

    }

    private QuizAttemptHistoriesEntity createOrUpdateQuizAttemptHistoriesEntity(QuestionsEntity questionsEntity, UsersEntity usersEntity, ChoicesEntity selectedChoiceEntity, ChoicesEntity correctChoiceEntity) {

        Optional<QuizAttemptHistoriesEntity> optionalQuizAttemptHistoriesEntity
                = quizAttemptHistoriesJpaRepository.findQuizAttemptHistoriesEntitiesByQuestionAndUser(questionsEntity, usersEntity);

        QuizAttemptHistoriesEntity quizAttemptHistoriesEntity = optionalQuizAttemptHistoriesEntity.orElseGet(() -> QuizAttemptHistoriesEntity.builder()
                .user(usersEntity)
                .question(questionsEntity)
                .selectedChoice(selectedChoiceEntity)
                .correctChoice(correctChoiceEntity)
                .attemptCount(0)
                .build()
        );

        quizAttemptHistoriesEntity.updateLatestSelectedChoice(selectedChoiceEntity);

        return quizAttemptHistoriesEntity;
    }
}
