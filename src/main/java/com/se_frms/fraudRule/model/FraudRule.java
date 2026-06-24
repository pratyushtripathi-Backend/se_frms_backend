package com.se_frms.fraudRule.model;

import com.se_frms.ruleCategory.model.RuleCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "se_frms_fraud_rule"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FraudRule {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    @ManyToOne
    @JoinColumn(
            name = "category_id"
    )
    private RuleCategory category;

    private String ruleCode;

    private String ruleName;

    private String ruleDescription;

    private Boolean status;

    private LocalDateTime createdAt;

    private Integer createdBy;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        this.createdAt =
                LocalDateTime.now();

        this.updatedAt =
                LocalDateTime.now();

    }

    @PreUpdate
    public void preUpdate() {

        this.updatedAt =
                LocalDateTime.now();

    }

}