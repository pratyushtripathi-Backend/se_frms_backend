package com.se_frms.user.model;

import com.se_frms.permission.model.UserPermission;
import com.se_frms.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_users_phone",
                        columnNames = "phone_number"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            nullable = false,
            updatable = false
    )
    private UUID id;

    @Column(
            name = "first_name",
            nullable = false,
            length = 50
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 50
    )
    private String lastName;

    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    @Column(
            name = "phone_number",
            nullable = false,
            unique = true,
            length = 15
    )
    private String phoneNumber;


    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private Role role = Role.EMPLOYEE;

    @Column(
            name = "is_active",
            nullable = false
    )
    @Builder.Default
    private Boolean isActive = true;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    private List<UserPermission>
            permissions;

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.isActive == null) {
            this.isActive = true;
        }

        if (this.role == null) {
            this.role = Role.EMPLOYEE;
        }
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedAt = LocalDateTime.now();
    }
}

