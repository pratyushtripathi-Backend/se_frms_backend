package com.se_frms.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/** Calls the Notification Service only after a committed admin-recipient change. */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationRecipientCacheRefreshClient {

    private final RestTemplate restTemplate;

    @Value("${notification-service.base-url:http://localhost:8096}")
    private String notificationServiceBaseUrl;

    @Value("${app.internal.api-key}")
    private String internalApiKey;

    public void refreshRecipientCache() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-INTERNAL-API-KEY", internalApiKey);
            restTemplate.exchange(
                    notificationServiceBaseUrl + "/api/v1/notifications/internal/recipients/refresh",
                    HttpMethod.POST,
                    new HttpEntity<Void>(headers),
                    Void.class
            );
            log.info("Notification recipient cache refresh requested successfully");
        } catch (Exception ex) {
            // Never fail an admin/role update due to a temporarily unavailable service.
            log.warn("Notification recipient cache refresh request failed; scheduled refresh will recover", ex);
        }
    }
}
