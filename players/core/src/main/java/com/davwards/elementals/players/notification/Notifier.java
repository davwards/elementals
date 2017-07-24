package com.davwards.elementals.players.notification;

import com.davwards.elementals.players.models.PlayerId;

public interface Notifier {
    void sendNotification(PlayerId id, Notification.NotificationType type);
}
