package com.se_frms.admin.dto;



import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEmployeePatchRequest {

    private String firstName;

    private String lastName;

    @Email
    private String email;

    private String phoneNumber;

    private Boolean isActive;
}