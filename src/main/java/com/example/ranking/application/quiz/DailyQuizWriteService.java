package com.example.ranking.application.quiz;

import com.example.ranking.domain.quiz.request.QuizCreateRequest;
import com.example.ranking.domain.quiz.request.QuizCreateRequest.QuizCreate;
import com.example.ranking.domain.quiz.request.QuizEditRequest;
import com.example.ranking.domain.quiz.request.QuizEditRequest.Choice;
import com.example.ranking.domain.quiz.request.QuizEditRequest.QuizEdit;
import com.example.ranking.domain.quiz.request.QuizSubmitRequest.UserAnswerChoice;
import com.example.ranking.global.exception.ErrorCode;
import com.example.ranking.global.exception.QuizException;
import com.example.ranking.infra.persistence.quiz.*;
import com.example.ranking.infra.persistence.quiz.jpa.*;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes;
import com.example.ranking.infra.persistence.user.UsersEntity;
import com.example.ranking.infra.persistence.user.jpa.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
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

        // 기존 문제 메타정보 수정
        QuestionsTitlesEntity questionsTitlesEntity = questionsTitlesJpaRepository.findByIdAndUserAndStatusWithJoinFetch(questionTitleId, usersEntity)
                .orElseThrow(() -> new QuizException(ErrorCode.QUESTION_TITLE_NOT_FOUND));
        questionsTitlesEntity.changeQuestionsEntityColumns(subjectsEntity, quizEdit);

        // 현재 디비에 저장된 질문 데이터 맵
        Map<Long, QuestionsEntity> questionsEntitiesFromDatabaseMap = new HashMap<>();
        for (QuestionsEntity questionsEntity : questionsTitlesEntity.getQuestions()) {
            questionsEntitiesFromDatabaseMap.put(questionsEntity.getId(), questionsEntity);
        }

        // 요청된 질문 데이터에 대해서 기존 데이터 수정 또는 신규 생성
        for (QuizEditRequest.Question question : quizEdit.questions()) {

            QuestionsEntity questionsEntity = updateOrSaveNewQuestionsEntity(question, subjectsEntity, questionsTitlesEntity);

            // 각각의 문항에 대한 업데이트
            //  현재 디비에 저장된 보기 데이터 맵
            Map<Long, ChoicesEntity> choicesEntitiesFromDatabaseMap = new HashMap<>();
            for (ChoicesEntity choicesEntity : Optional.ofNullable(questionsEntity.getChoices()).orElse(Collections.emptyList())) {
                choicesEntitiesFromDatabaseMap.put(choicesEntity.getId(), choicesEntity);
            }

            //  요청된 보기 데이터에 대해서 기존 데이터 수정 또는 신규 생성
            for (Choice choice : question.choices()) {

                // 보기 데이터 수정 및 신규 생성
                updateOrCreateNewChoicesEntity(choice, choicesEntitiesFromDatabaseMap, subjectsEntity, questionsEntity);
                choicesEntitiesFromDatabaseMap.remove(choice.choiceId());

            }

            questionsEntitiesFromDatabaseMap.remove(question.questionId());
            // TODO 상태 칼럼 추가로 수정
            choicesJpaRepository.deleteAll(choicesEntitiesFromDatabaseMap.values());
        }

        questionsEntitiesFromDatabaseMap.values().forEach(QuestionsEntity::deactivate);

    }

    private QuestionsEntity updateOrSaveNewQuestionsEntity(QuizEditRequest.Question question, SubjectsEntity subjectsEntity, QuestionsTitlesEntity questionsTitlesEntity) {

        Optional<QuestionsEntity> optionalQuestionsEntity = questionsJpaRepository.findQuestionWithChoicesById(question.questionId());

        // 기존 질문 수정
        QuestionsEntity questionsEntity;
        if (optionalQuestionsEntity.isPresent()) {
            questionsEntity = optionalQuestionsEntity.get();
            questionsEntity.changeQuestionsEntityColumns(subjectsEntity, questionsTitlesEntity, question);
        }
        // 신규 질문 생성
        else {
            questionsEntity = QuestionsEntity.builder()
                    .subject(subjectsEntity)
                    .questionTitle(questionsTitlesEntity)
                    .questionType(QuizEntityTypes.QuestionType.MULTIPLE_CHOICE)
                    .questionText(question.questionText())
                    .status(QuizEntityTypes.QuestionStatus.ACTIVE)
                    .build();
        }
        return questionsJpaRepository.save(questionsEntity);
    }

    private void updateOrCreateNewChoicesEntity(Choice choice, Map<Long, ChoicesEntity> choicesEntitiesFromDatabaseMap, SubjectsEntity subjectsEntity, QuestionsEntity questionsEntity) {
        ChoicesEntity choicesEntity;
        if (choicesEntitiesFromDatabaseMap.containsKey(choice.choiceId())) {
            choicesEntity = choicesEntitiesFromDatabaseMap.get(choice.choiceId());
            choicesEntity.changePrevChoicesEntityColumnsByChoice(subjectsEntity, questionsEntity, choice);
        } else {
            choicesEntity = ChoicesEntity.builder()
                    .subject(subjectsEntity)
                    .question(questionsEntity)
                    .text(choice.choiceText())
                    .isCorrect(choice.isCorrect())
                    .build();
        }
        choicesJpaRepository.save(choicesEntity);
    }

    @Transactional
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

}
