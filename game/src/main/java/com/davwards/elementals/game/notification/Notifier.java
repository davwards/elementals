package com.davwards.elementals.game.notification;

import com.davwards.elementals.game.players.models.PlayerId;

public interface Notifier {
    void sendNotification(PlayerId id, Notification.NotificationType type);
}
