package com.davwards.elementals.api.dev;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.notification.Notification;
import com.davwards.elementals.players.notification.Notifier;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Profile("fake-notifications")
public class NotificationController implements Notifier {

    private List<NotificationResponse> notifications = new ArrayList<>();

    @RequestMapping(value = "/test/notifications", method = RequestMethod.GET)
    public List<NotificationResponse> getNotifications() {
        return notifications;
    }

    @Override
    public void sendNotification(PlayerId id, Notification.NotificationType type) {
        notifications.add(new NotificationResponse(id.toString(), messageFor(type)));
    }

    private String messageFor(Notification.NotificationType type) {
        switch(type) {
            case PLAYER_HAS_DIED:
                return "You have died!";
            default:
                throw new RuntimeException("Unknown notification type: " + type);
        }
    }

    private static class NotificationResponse {
        @JsonProperty
        private final String playerId;

        @JsonProperty
        private final String message;

        private NotificationResponse(String playerId, String message) {
            this.playerId = playerId;
            this.message = message;
        }
    }
}
