package com.se_frms.notification.event;

/**
 * Published when a user's role changes. The listener refreshes the Notification
 * Service recipient cache after the database transaction commits successfully.
 */
public record AdminRecipientChangedEvent(Integer userId) {
}
