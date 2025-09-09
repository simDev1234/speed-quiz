package com.example.ranking.application.quiz;

import com.example.ranking.domain.quiz.response.QuizDetailResponse.QuizDetail;
import com.example.ranking.domain.quiz.response.QuizListResponse;
import com.example.ranking.domain.quiz.response.QuizListResponse.Question;
import com.example.ranking.domain.quiz.response.QuizListResponse.QuestionTitle;
import com.example.ranking.domain.quiz.response.QuizResultResponse.PersonalQuizResult;
import com.example.ranking.domain.quiz.response.QuizResultResponse.QuizResult;
import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.QuizAttemptHistoriesEntity;
import com.example.ranking.infra.persistence.quiz.jpa.QuestionsJpaRepository;
import com.example.ranking.infra.persistence.quiz.jpa.QuestionsTitlesJpaRepository;
import com.example.ranking.infra.persistence.quiz.jpa.QuizAttemptHistoriesJpaRepository;
import com.example.ranking.infra.persistence.quiz.jpa.SubjectsJpaRepository;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionStatus;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.SubjectStatus;
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
    private final SubjectsJpaRepository  subjectsJpaRepository;
    private final QuestionsTitlesJpaRepository  questionsTitlesJpaRepository;
    private final QuestionsJpaRepository questionsJpaRepository;
    private final QuizAttemptHistoriesJpaRepository quizAttemptHistoriesJpaRepository;

    public List<QuestionTitle> findPopularActiveQuestionTitles() {
        
        return questionsTitlesJpaRepository.findAllQuestionTitlesWithParticipationCountOrderByParticipationCountDesc();
    }

    public List<Question> findAllActiveQuestionsByQuestionTitleId(Long questionTitleId) {

        return questionsJpaRepository.findQuestionsWithChoicesByQuestionTitleIdAndStatus(questionTitleId, QuestionStatus.ACTIVE)
                .stream()
                .map(Question::fromEntity)
                .toList();

    }

    public PersonalQuizResult findUserQuizAttemptHistories(String email, Long questionTitleId) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        QuestionsTitlesEntity questionsTitlesEntity = questionsTitlesJpaRepository.findById(questionTitleId)
                .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_TITLE_NOT_FOUND));

        List<QuizAttemptHistoriesEntity> latestUserQuizAttemptHistoryList
                = quizAttemptHistoriesJpaRepository.findQuizAttemptHistoriesEntitiesByQuestionStatusAndUserAndQuestionTitle(QuestionStatus.ACTIVE , usersEntity, questionsTitlesEntity);

        if (latestUserQuizAttemptHistoryList.isEmpty()) {
            return PersonalQuizResult.builder()
                    .questionTitleId(questionsTitlesEntity.getId())
                    .finalScore(new BigDecimal(0))
                    .finalAccuracyRate(new BigDecimal(0))
                    .correctAnswerCounts(0)
                    .totalQuestions(0)
                    .questionResults(new ArrayList<>())
                    .build();
        }

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
                .questionTitleId(questionsTitlesEntity.getId())
                .finalScore(finalScore)
                .finalAccuracyRate(finalAccuracyRate)
                .correctAnswerCounts(correctCounts)
                .totalQuestions(totalQuestions)
                .questionResults(questionResults)
                .build();
    }

    public List<QuestionTitle> findAllMyActiveQuizList(String email) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        return questionsTitlesJpaRepository.findAllByStatusAndUser(QuestionTitleStatus.ACTIVE, usersEntity)
                .stream()
                .map(QuestionTitle::fromEntity)
                .toList();

    }


    public List<QuizListResponse.Subject> findAllSubjects() {
        return subjectsJpaRepository.findAllByStatus(SubjectStatus.ACTIVE)
                .stream()
                .map(QuizListResponse.Subject::fromEntity)
                .toList();
    }

    public QuizDetail findQuizDetailByQuestionTitleIdAndUser(Long questionTitleId, String email) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        QuestionsTitlesEntity questionsTitlesEntity = questionsTitlesJpaRepository.findByIdAndUserAndStatusWithJoinFetch(questionTitleId, usersEntity)
                .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_TITLE_NOT_FOUND));

        return QuizDetail.fromEntity(questionsTitlesEntity);
    }

}
