package com.example.ranking.infra.persistence.quiz;

import com.example.ranking.infra.persistence.BasicEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.QuestionTitleStatus;
import com.example.ranking.infra.persistence.user.UsersEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "questions_titles")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionsTitlesEntity extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UsersEntity user;

    @Lob
    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Enumerated(EnumType.STRING)
    private QuestionTitleStatus status;

}
