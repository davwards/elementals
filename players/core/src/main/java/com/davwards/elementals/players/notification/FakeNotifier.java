package com.davwards.elementals.players.notification;

import com.davwards.elementals.players.models.PlayerId;

import java.util.ArrayList;
import java.util.List;

public class FakeNotifier implements Notifier {
    private List<Notification> outbox = new ArrayList<>();

    public List<Notification> notificationsSent() {
        return outbox;
    }

    @Override
    public void sendNotification(PlayerId id, Notification.NotificationType type) {
        outbox.add(new Notification(id, type));
    }
}
