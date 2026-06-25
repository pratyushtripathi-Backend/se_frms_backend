package com.se_frms.ruleScore.model;

import com.se_frms.fraudRule.model.FraudRule;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "se_frms_rule_score",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rule_score_rule_id",
                        columnNames = "rule_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "rule_id",
            nullable = false
    )
    private FraudRule rule;

    @Column(
            nullable = false
    )
    private Integer score;

    @Builder.Default
    private Boolean status = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        this.createdAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();

        if (this.status == null) {
            this.status = true;
        }
    }

    @PreUpdate
    public void preUpdate() {

        this.updatedAt =
                LocalDateTime.now();
    }
}