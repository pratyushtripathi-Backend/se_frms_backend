package com.se_frms.auth.model;



import com.se_frms.user.model.User;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name =
                "se_frms_login_attempt"
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private User user;

    private String email;

    @Column(
            name =
                    "attempt_status"
    )
    private Boolean attemptStatus;

    @Column(
            name =
                    "failure_reason"
    )
    private String failureReason;

    @Column(
            name =
                    "ip_address"
    )
    private String ipAddress;

    @Column(
            precision = 10,
            scale = 7
    )
    private BigDecimal latitude;

    @Column(
            precision = 10,
            scale = 7
    )
    private BigDecimal longitude;

    private String url;

    @Column(
            name =
                    "attempted_at"
    )
    private LocalDateTime attemptedAt;

    @ManyToOne
    @JoinColumn(
            name = "created_by",
            nullable = true
    )
    private User createdBy;

    @PrePersist
    public void init() {

        attemptedAt =
                LocalDateTime.now();
    }
}