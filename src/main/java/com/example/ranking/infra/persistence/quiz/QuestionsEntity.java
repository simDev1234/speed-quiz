package com.example.ranking.infra.persistence.quiz;

import com.example.ranking.domain.quiz.request.QuizEditRequest;
import com.example.ranking.infra.persistence.BasicEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.*;

@Table(name = "questions")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionsEntity extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectsEntity subject;

    @ManyToOne
    @JoinColumn(name = "question_title_id")
    private QuestionsTitlesEntity questionTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType questionType;

    @Lob
    @Column(name = "question_text")
    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChoicesEntity> choices = new ArrayList<>();

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuizAttemptHistoriesEntity> quizAttemptHistories = new ArrayList<>();

    public void changeQuestionsEntityColumns(SubjectsEntity subjectsEntity,
                                             QuestionsTitlesEntity questionsTitlesEntity,
                                             QuizEditRequest.Question question){
        this.subject = subjectsEntity;
        this.questionTitle = questionsTitlesEntity;
        this.questionText = question.questionText();
    }

    public void deactivate() {
        this.status = QuestionStatus.DELETED;
    }
}
