package com.se_frms.notification.listener;

import com.se_frms.notification.event.AdminRecipientChangedEvent;
import com.se_frms.notification.service.NotificationRecipientCacheRefreshClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminRecipientCacheRefreshListener {

    private final NotificationRecipientCacheRefreshClient refreshClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAdminRecipientChanged(AdminRecipientChangedEvent event) {
        log.info("Refreshing notification recipients after committed role change, userId={}", event.userId());
        refreshClient.refreshRecipientCache();
    }
}
