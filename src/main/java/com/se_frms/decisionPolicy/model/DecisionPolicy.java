package com.se_frms.decisionPolicy.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "se_frms_decision_policy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "allow_min_score", nullable = false)
    private Integer allowMinScore;

    @Column(name = "allow_max_score", nullable = false)
    private Integer allowMaxScore;

    @Column(name = "review_min_score", nullable = false)
    private Integer reviewMinScore;

    @Column(name = "review_max_score", nullable = false)
    private Integer reviewMaxScore;

    @Column(name = "block_min_score", nullable = false)
    private Integer blockMinScore;

    @Column(name = "block_max_score", nullable = false)
    private Integer blockMaxScore;

    @Builder.Default
    @Column(nullable = false)
    private Boolean status = true;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (status == null) {
            status = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
