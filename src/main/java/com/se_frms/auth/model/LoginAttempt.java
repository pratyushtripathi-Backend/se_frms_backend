package com.se_frms.auth.model;

import com.se_frms.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "se_frms_login_attempts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String email;

    @Column(name = "attempt_date")
    private LocalDate attemptDate;

    @Column(name = "attempt_time")
    private LocalTime attemptTime;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Builder.Default
    private Boolean status = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        if (attemptDate == null) {
            attemptDate = now.toLocalDate();
        }
        if (attemptTime == null) {
            attemptTime = now.toLocalTime();
        }
        if (status == null) {
            status = true;
        }
        if (createdDate == null) {
            createdDate = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}