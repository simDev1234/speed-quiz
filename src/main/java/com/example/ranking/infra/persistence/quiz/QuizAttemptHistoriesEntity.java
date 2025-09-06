package com.example.ranking.infra.persistence.quiz;

import com.example.ranking.infra.persistence.BasicEntity;
import com.example.ranking.infra.persistence.user.UsersEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "quiz_attempt_histories")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptHistoriesEntity extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersEntity user;

    @ManyToOne
    @JoinColumn(name = "question_title_id")
    private QuestionsTitlesEntity questionTitle;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionsEntity question;

    @ManyToOne
    @JoinColumn(name = "selected_choice_id")
    private ChoicesEntity selectedChoice;

    @ManyToOne
    @JoinColumn(name = "correct_choice_id")
    private ChoicesEntity correctChoice;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    public void incrementAttemptCount() {
        this.attemptCount++;
    }

    public void updateLatestSelectedChoice(ChoicesEntity choicesEntity) {
        this.selectedChoice = choicesEntity;
    }

}
