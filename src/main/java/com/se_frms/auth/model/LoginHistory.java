package com.se_frms.auth.model;



import com.se_frms.user.model.User;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name =
                "se_frms_login_history"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(
            strategy =
                    GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch =
                    FetchType.LAZY
    )
    @JoinColumn(
            name =
                    "user_id",
            nullable =
                    false
    )
    private User user;

    @Column(
            name =
                    "login_date"
    )
    private LocalDate loginDate;

    @Column(
            name =
                    "login_time"
    )
    private LocalTime loginTime;

    @Column(
            name =
                    "ip_address",
            length =
                    50
    )
    private String ipAddress;

    @Column(
            name =
                    "mac_address",
            length =
                    50
    )
    private String macAddress;

    @Column(
            name = "latitude"
    )
    private Double latitude;

    @Column(
            name = "longitude"
    )
    private Double longitude;


    @Column(
            columnDefinition =
                    "TEXT"
    )
    private String url;

    @Column(
            nullable =
                    false
    )
    @Builder.Default
    private Boolean status =
            true;

    @ManyToOne(
            fetch =
                    FetchType.LAZY
    )
    @JoinColumn(
            name =
                    "created_by"
    )
    private User createdBy;

    @Column(
            name =
                    "created_date"
    )
    private LocalDateTime createdDate;

    @Column(
            name =
                    "updated_at"
    )
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (
                loginDate
                        ==
                        null
        ) {

            loginDate =
                    LocalDate.now();
        }

        if (
                loginTime
                        ==
                        null
        ) {

            loginTime =
                    LocalTime.now();
        }

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
