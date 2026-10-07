package com.se_frms.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/**
 * The profile image bytes of a user, stored in the profile_image (bytea) column
 * of se_frms_user_master - the same table as User.
 *
 * Kept out of the User entity on purpose: User is loaded on every authenticated
 * request and in every user list, and the image (up to 2 MB) must not be read
 * each time. This read-only view is only used when the image itself is requested.
 * Writes go through UserProfileImageRepository's UPDATE queries; a row is never
 * inserted through this entity (User creates the row).
 */
@Entity
@Immutable
@Table(name = "se_frms_user_master")
@Getter
@NoArgsConstructor
public class UserProfileImage {

    @Id
    private Integer id;

    @Column(name = "profile_image")
    private byte[] profileImage;
}
