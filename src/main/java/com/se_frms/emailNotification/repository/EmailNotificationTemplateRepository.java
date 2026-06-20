package com.se_frms.emailNotification.repository;

import com.se_frms.emailNotification.model.EmailNotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EmailNotificationTemplateRepository
        extends JpaRepository<EmailNotificationTemplate, Integer>,
        JpaSpecificationExecutor<EmailNotificationTemplate> {

    Optional<EmailNotificationTemplate> findByTemplateCode(String templateCode);

    Optional<EmailNotificationTemplate> findByTemplateCodeAndStatus(
            String templateCode,
            Boolean status
    );

    boolean existsByTemplateCode(String templateCode);
}
