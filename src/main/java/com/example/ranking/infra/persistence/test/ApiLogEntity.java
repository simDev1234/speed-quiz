package com.example.ranking.infra.persistence.test;

import com.example.ranking.infra.persistence.BasicEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Table(name = "api_log_entity")
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@Slf4j
public class ApiLogEntity extends BasicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String clientIp;

    private String requestPath;
    private String requestMethod;
    private Integer responseStatus;

    private String requestParam;
    private String requestBody;
    private String response;

    private Long count = 0L;
    private LocalDateTime loggedAt;

    public void incrementCount() {
        this.count += 1;
    }
}
