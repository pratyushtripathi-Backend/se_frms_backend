package com.se_frms.user.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.lang.Integer;

@Entity
@Table(
        name = "se_frms_user_master"
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )
    private Integer id;

    @Column(
            name = "first_name"
    )
    private String firstName;

    @Column(
            name = "last_name"
    )
    private String lastName;

    @Column(
            unique = true,
            nullable = false
    )
    private String email;

    @Column(
            name = "phone_number"
    )
    private String phoneNumber;

    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;

    @Column(
            name = "user_type",
            nullable = false
    )
    private String userType;

    @Builder.Default
    private Boolean status = true;

    @Builder.Default
    @Column(name = "failed_password_attempts")
    private Integer failedPasswordAttempts = 0;

    @Column(name = "password_locked_until")
    private LocalDateTime passwordLockedUntil;

    @ManyToOne
    @JoinColumn(
            name = "created_by"
    )
    private User createdBy;

    @Column(
            name = "created_date"
    )
    private LocalDateTime createdDate;

    @Column(
            name = "updated_at"
    )
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        createdDate =
                LocalDateTime.now();

        updatedAt =
                LocalDateTime.now();

    }

    @PreUpdate
    public void preUpdate() {

        updatedAt =
                LocalDateTime.now();

    }

}
