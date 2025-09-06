package com.example.ranking.application.quiz;

import com.example.ranking.domain.quiz.request.QuizCreateRequest;
import com.example.ranking.domain.quiz.request.QuizCreateRequest.*;
import com.example.ranking.domain.quiz.request.QuizEditRequest;
import com.example.ranking.domain.quiz.request.QuizEditRequest.*;
import com.example.ranking.domain.quiz.request.QuizSubmitRequest.UserAnswerChoice;
import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.persistence.quiz.*;
import com.example.ranking.infra.persistence.quiz.jpa.*;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyQuizWriteService {

    private final UsersJpaRepository usersJpaRepository;
    private final QuestionsTitlesJpaRepository questionsTitlesJpaRepository;
    private final QuestionsJpaRepository questionsJpaRepository;
    private final ChoicesJpaRepository choicesJpaRepository;
    private final QuizAttemptHistoriesJpaRepository quizAttemptHistoriesJpaRepository;
    private final SubjectsJpaRepository subjectsJpaRepository;

    public void saveNewQuiz(String email, QuizCreate quizCreate) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        SubjectsEntity subjectsEntity = subjectsJpaRepository.findById(quizCreate.subjectId())
                .orElseThrow(() -> new QuizException(ErrorCode.QUIZ_SUBJECT_NOT_FOUND));

        QuestionsTitlesEntity questionsTitlesEntity = questionsTitlesJpaRepository.save(QuizCreate.toQuestionsTitlesEntity(subjectsEntity, usersEntity, quizCreate));

        for (QuizCreateRequest.Question question : quizCreate.questions()) {
            QuestionsEntity questionsEntity = questionsJpaRepository.save(QuizCreateRequest.Question.toQuestionsEntity(subjectsEntity, questionsTitlesEntity, question));
            List<ChoicesEntity> choicesEntities = choicesJpaRepository.saveAll(QuizCreateRequest.Question.toChoicesEntities(subjectsEntity, questionsEntity, question));
        }

    }

    @Transactional
    public void editExistingQuiz(String email, Long questionTitleId, QuizEdit quizEdit) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        SubjectsEntity subjectsEntity = subjectsJpaRepository.findById(quizEdit.subjectId())
                .orElseThrow(() -> new QuizException(ErrorCode.QUIZ_SUBJECT_NOT_FOUND));

        QuestionsTitlesEntity questionsTitlesEntity = questionsTitlesJpaRepository.findByIdAndUserWithJoinFetch(questionTitleId, usersEntity)
                .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_TITLE_NOT_FOUND));
        questionsTitlesEntity.changeQuestionsEntityColumns(subjectsEntity, quizEdit);

        List<QuizEditRequest.Question> questions = quizEdit.questions();
        for (int i = 0; i < questions.size(); i++) {

            QuizEditRequest.Question curQuestion = questions.get(i);
            QuestionsEntity prevQuestionsEntity = questionsJpaRepository.findQuestionWithChoicesById(curQuestion.questionId())
                    .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_NOT_FOUND));
            prevQuestionsEntity.changeQuestionsEntityColumns(subjectsEntity, questionsTitlesEntity, curQuestion);

            for (int j = 0; j < curQuestion.choices().size(); j++) {
                Choice curChoice = curQuestion.choices().get(j);
                ChoicesEntity prevChoicesEntity = prevQuestionsEntity.getChoices().get(j);
                prevChoicesEntity.changePrevChoicesEntityColumnsByChoice(subjectsEntity, prevQuestionsEntity, curChoice);
            }

        }

    }

    public void saveQuizAnswers(String email, List<UserAnswerChoice> userAnswerChoices) {

        UsersEntity usersEntity = usersJpaRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new QuizException(ErrorCode.USER_NOT_FOUND));

        validateIfAnswersEmpty(userAnswerChoices);

        QuestionsTitlesEntity questionsTitlesEntity = questionsTitlesJpaRepository.findById(userAnswerChoices.getFirst().questionTitleId())
                .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_TITLE_NOT_FOUND));

        List<QuizAttemptHistoriesEntity> bufferedQuizAttemptHistoriesList = new ArrayList<>();

        for (UserAnswerChoice userAnswerChoice : userAnswerChoices) {

            QuestionsEntity questionsEntity = questionsJpaRepository.findById(userAnswerChoice.questionId())
                    .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_NOT_FOUND));

            ChoicesEntity correctChoiceEntity = questionsEntity.getChoices().stream()
                    .filter(ChoicesEntity::getIsCorrect).findFirst()
                    .orElseThrow(() -> new QuizException(ErrorCode.CHOICE_NOT_FOUND));

            ChoicesEntity selectedChoiceEntity = Objects.isNull(userAnswerChoice.selectedChoiceId()) ?
                    null : choicesJpaRepository.findByIdWithJoinedFetchQuestion(userAnswerChoice.selectedChoiceId())
                            .orElseThrow(() -> new QuizException(ErrorCode.CHOICE_NOT_FOUND));

            QuizAttemptHistoriesEntity quizAttemptHistoriesEntity = createOrUpdateQuizAttemptHistoriesEntity(
                    questionsTitlesEntity, questionsEntity, usersEntity, selectedChoiceEntity, correctChoiceEntity
            );

            quizAttemptHistoriesEntity.incrementAttemptCount();

            bufferedQuizAttemptHistoriesList.add(quizAttemptHistoriesEntity);

        }

        quizAttemptHistoriesJpaRepository.saveAll(bufferedQuizAttemptHistoriesList);

    }

    private static void validateIfAnswersEmpty(List<UserAnswerChoice> userAnswerChoices) {
        if (userAnswerChoices.isEmpty()) {
            throw new QuizException(ErrorCode.INVALID_INPUT);
        }
    }

    private QuizAttemptHistoriesEntity createOrUpdateQuizAttemptHistoriesEntity(QuestionsTitlesEntity questionsTitlesEntity, QuestionsEntity questionsEntity, UsersEntity usersEntity, ChoicesEntity selectedChoiceEntity, ChoicesEntity correctChoiceEntity) {

        Optional<QuizAttemptHistoriesEntity> optionalQuizAttemptHistoriesEntity
                = quizAttemptHistoriesJpaRepository.findQuizAttemptHistoriesEntitiesByQuestionAndUser(questionsEntity, usersEntity);

        QuizAttemptHistoriesEntity quizAttemptHistoriesEntity = optionalQuizAttemptHistoriesEntity.orElseGet(() -> QuizAttemptHistoriesEntity.builder()
                .user(usersEntity)
                .questionTitle(questionsTitlesEntity)
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
