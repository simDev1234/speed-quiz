package com.example.ranking.domain.quiz.request;

import com.example.ranking.infra.persistence.quiz.ChoicesEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsEntity;
import com.example.ranking.infra.persistence.quiz.QuestionsTitlesEntity;
import com.example.ranking.infra.persistence.quiz.SubjectsEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.*;
import com.example.ranking.infra.persistence.user.UsersEntity;
import java.util.ArrayList;
import java.util.List;

public abstract class QuizCreateRequest {

    public record QuizCreate(
            Long subjectId,
            String title,
            String description,
            Integer timeLimit,
            List<Question> questions
    ){

        public static QuestionsTitlesEntity toQuestionsTitlesEntity(SubjectsEntity subjectsEntity, UsersEntity usersEntity, QuizCreate quizCreate){
            return QuestionsTitlesEntity.builder()
                    .user(usersEntity)
                    .subjectsEntity(subjectsEntity)
                    .title(quizCreate.title)
                    .description(quizCreate.description)
                    .timeLimit(quizCreate.timeLimit())
                    .status(QuestionTitleStatus.ACTIVE)
                    .build();
        }
    }

    public record Question(
            String questionText,
            List<String> choices,
            Long correctAnswerIndex
    ){
        public static QuestionsEntity toQuestionsEntity(SubjectsEntity subjectsEntity, QuestionsTitlesEntity questionsTitlesEntity, Question question){

            return QuestionsEntity.builder()
                    .subject(subjectsEntity)
                    .questionTitle(questionsTitlesEntity)
                    .questionType(QuestionType.MULTIPLE_CHOICE)
                    .questionText(question.questionText())
                    .status(QuestionStatus.ACTIVE)
                    .build();
        }

        public static List<ChoicesEntity> toChoicesEntities(SubjectsEntity subjectsEntity, QuestionsEntity questionsEntity, Question question){
            List<ChoicesEntity> choices = new ArrayList<>();

            for (int i = 0; i < question.choices.size(); i++) {
                boolean isCorrect = false;
                if (i == question.correctAnswerIndex) {
                    isCorrect = true;
                }
                String choiceName = question.choices().get(i);
                choices.add(Question.toChoicesEntity(subjectsEntity, questionsEntity, choiceName, isCorrect));
            }

            return choices;
        }

        public static ChoicesEntity toChoicesEntity(SubjectsEntity subjectsEntity, QuestionsEntity questionsEntity, String choiceName, boolean isCorrect){
            return ChoicesEntity.builder()
                    .subject(subjectsEntity)
                    .question(questionsEntity)
                    .text(choiceName)
                    .isCorrect(isCorrect)
                    .build();
        }

        public static ChoicesEntity toChoicesEntity(SubjectsEntity subjectsEntity, QuestionsEntity questionsEntity, ChoicesEntity choicesEntity, String choiceName, boolean isCorrect){
            return ChoicesEntity.builder()
                    .id(choicesEntity.getId())
                    .subject(subjectsEntity)
                    .question(questionsEntity)
                    .text(choiceName)
                    .isCorrect(isCorrect)
                    .build();
        }
    }

}
