package com.example.ranking.infra.persistence.quiz.type;

public abstract class QuizEntityTypes {

    public enum QuestionType {
        MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_BLANK
    }

    public enum SubjectStatus {
        ACTIVE, INACTIVE, DELETED
    }

    public enum QuestionTitleStatus {
        ACTIVE, INACTIVE, DELETED
    }

    public enum QuestionStatus {
        ACTIVE, INACTIVE, DELETED
    }

}
