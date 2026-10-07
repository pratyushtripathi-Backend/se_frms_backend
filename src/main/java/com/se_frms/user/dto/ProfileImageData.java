package com.se_frms.user.dto;

/** A user's profile image as returned by GET /api/v1/users/{id}/profile-image. */
public record ProfileImageData(
        byte[] data,
        String contentType
) {
}
