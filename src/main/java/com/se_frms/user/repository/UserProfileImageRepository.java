package com.se_frms.user.repository;

import com.se_frms.user.model.UserProfileImage;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileImageRepository
        extends JpaRepository<UserProfileImage, Integer> {

    /** Stores (or replaces) the image and its MIME type. Returns rows updated (0 = user not found). */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE se_frms_user_master
            SET profile_image = :image,
                profile_image_content_type = :contentType,
                updated_at = :now
            WHERE id = :id
            """, nativeQuery = true)
    int updateProfileImage(@Param("id") Integer id,
                           @Param("image") byte[] image,
                           @Param("contentType") String contentType,
                           @Param("now") LocalDateTime now);

    /** Removes the image. Returns rows updated (0 = user not found). */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
            UPDATE se_frms_user_master
            SET profile_image = NULL,
                profile_image_content_type = NULL,
                updated_at = :now
            WHERE id = :id
            """, nativeQuery = true)
    int clearProfileImage(@Param("id") Integer id,
                          @Param("now") LocalDateTime now);
}
