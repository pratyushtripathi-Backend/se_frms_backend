package com.se_frms.user.dto;



import lombok.Builder;
import lombok.Data;

import java.lang.Integer;

@Data
@Builder
public class UserResponseDTO {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String role;
}
