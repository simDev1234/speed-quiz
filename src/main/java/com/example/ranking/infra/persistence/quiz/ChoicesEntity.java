package com.example.ranking.infra.persistence.quiz;

import com.example.ranking.infra.persistence.BasicEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "choices")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoicesEntity extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private SubjectsEntity subject;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionsEntity question;

    private String text;

    @Column(name = "is_correct")
    private Boolean isCorrect;

}
