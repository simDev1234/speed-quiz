package com.example.ranking.infra.persistence.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Table(name = "choice_attempts_histories")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoiceAttemptHistoriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_attempt_id")
    private QuizAttemptHistoriesEntity quizAttempt;

    @ManyToOne
    @JoinColumn(name = "choice_id")
    private ChoicesEntity choice;

    @Column(name = "is_selected")
    private Boolean isSelected;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

}
