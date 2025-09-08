package com.example.ranking.infra.persistence.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Table(name = "user_email_auth")
@Entity
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserEmailAuthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "email")
    private String email;
    @Column(name = "code")
    private String code;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "expirated_at")
    private LocalDateTime expiratedAt;

    public void resetExpiratedAt(){
        this.expiratedAt = this.createdAt;
    }

}
