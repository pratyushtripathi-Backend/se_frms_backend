package com.se_frms.blacklistEntry.model;

import com.se_frms.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "se_frms_blacklist_entry")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // IP / DEVICE / LOCATION
    @Column(name = "type", nullable = false, length = 20)
    private String type;

    // The actual IP address, device id, or location name being blacklisted
    @Column(name = "value", nullable = false, length = 255)
    private String value;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "risk_type", length = 100)
    private String riskType;

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
