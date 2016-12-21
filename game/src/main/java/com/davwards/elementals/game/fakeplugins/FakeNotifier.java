package com.davwards.elementals.game.fakeplugins;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.notification.Notification;
import com.davwards.elementals.game.notification.Notifier;

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
