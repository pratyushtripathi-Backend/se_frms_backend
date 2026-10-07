package com.se_frms.user.dto;



import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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

    private Boolean status;

    // true when the user has a profile image
    private Boolean hasProfileImage;

    // GET this (with the login token) to load the image; null when there is none.
    // The ?v= part changes on every upload, so a cached old image is never shown.
    private String profileImageUrl;

    private String createdBy;

    private LocalDateTime createdDate;

    private LocalDateTime updatedAt;

    private Boolean logoutRequired;
}
