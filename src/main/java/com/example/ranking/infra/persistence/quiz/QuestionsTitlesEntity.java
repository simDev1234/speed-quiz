package com.example.ranking.infra.persistence.quiz;

import com.example.ranking.domain.quiz.request.QuizEditRequest;
import com.example.ranking.infra.persistence.BasicEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "questions_titles")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionsTitlesEntity extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectsEntity subjectsEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersEntity user;

    @Lob
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "questionTitle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuestionsEntity> questions;

    @OneToMany(mappedBy = "questionTitle", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuizAttemptHistoriesEntity> quizAttemptHistories;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Enumerated(EnumType.STRING)
    private QuestionTitleStatus status;

    public void changeQuestionsEntityColumns(SubjectsEntity subjectsEntity, QuizEditRequest.QuizEdit quizEdit){
        this.subjectsEntity = subjectsEntity;
        this.title = quizEdit.titleText();
        this.description = quizEdit.description();
        this.timeLimit = quizEdit.timeLimit();
    }

    public void deactivate() {
        this.status = QuestionTitleStatus.DELETED;
    }
}
