package com.se_frms.auth.model;

import com.se_frms.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "se_frms_login_attempt_count")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "attempt_type", nullable = false, length = 20)
    private String attemptType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_otp_id")
    private EmailOtp emailOtp;

    @Builder.Default
    @Column(name = "failed_attempts", nullable = false)
    private Integer failedAttempts = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean locked = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean status = true;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "lock_reason", length = 500)
    private String lockReason;

    @Builder.Default
    @Column(name = "admin_notification_sent", nullable = false)
    private Boolean adminNotificationSent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}