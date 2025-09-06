package com.example.ranking.infra.persistence.quiz;

import com.example.ranking.infra.persistence.BasicEntity;
import com.example.ranking.infra.persistence.quiz.type.QuizEntityTypes.SubjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "subjects")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectsEntity extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private SubjectStatus status;

}
