package com.se_frms.admin.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class EmployeeSummaryDTO {

    private UUID id;

    private String firstName;

    private String lastName;

    private String email;
}
