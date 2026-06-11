package com.se_frms.admin.dto;

import jdk.jshell.Snippet;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.lang.Integer;


@Getter
@Builder
public class EmployeeResponseDTO {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String role;


}
