package com.se_frms.emailNotification.service;

import com.se_frms.emailNotification.dto.EmailNotificationTemplateRequestDTO;
import com.se_frms.emailNotification.dto.EmailNotificationTemplateResponseDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

public interface EmailNotificationTemplateService {

    EmailNotificationTemplateResponseDTO createTemplate(
            EmailNotificationTemplateRequestDTO request
    );

    EmailNotificationTemplateResponseDTO updateTemplate(
            String templateCode,
            EmailNotificationTemplateRequestDTO request
    );

    EmailNotificationTemplateResponseDTO getTemplateByCode(
            String templateCode
    );


   Page<EmailNotificationTemplateResponseDTO> getAllTemplates(
           int page,
           int size,
           Map<String, String> filters
   );

    String getSubject(String templateCode);

    String renderTemplate(
            String templateCode,
            Map<String, String> values
    );
}
