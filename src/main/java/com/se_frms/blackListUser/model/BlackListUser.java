package com.se_frms.blackListUser.model;

import com.se_frms.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "se_frms_blacklist_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackListUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "employee_name", nullable = false, length = 150)
    private String employeeName;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "mobile", length = 15)
    private String mobile;

    @Builder.Default
    @Column(name = "status")
    private Boolean status = true;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "risk_type", length = 100)
    private String riskType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

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
