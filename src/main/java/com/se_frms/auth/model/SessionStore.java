package com.se_frms.auth.model;

import com.se_frms.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "se_frms_session_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "session_active_date")
    private LocalDate sessionActiveDate;

    @Column(name = "session_active_time")
    private LocalTime sessionActiveTime;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private String token;

    @Builder.Default
    @Column(name = "status")
    private Boolean status = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (sessionActiveDate == null) {
            sessionActiveDate = LocalDate.now();
        }

        if (sessionActiveTime == null) {
            sessionActiveTime = LocalTime.now();
        }

        if (status == null) {
            status = true;
        }

        createdDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}