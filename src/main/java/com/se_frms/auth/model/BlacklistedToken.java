package com.se_frms.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "se_frms_blacklisted_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true,
            columnDefinition = "TEXT"
    )
    private String token;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;
}