package com.se_frms.admin.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.lang.Integer;

@Getter
@Builder
public class EmployeeSummaryDTO {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String role;

    private Boolean status;

    private Integer createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;
}
