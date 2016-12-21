package com.davwards.elementals.game.notification;

import com.davwards.elementals.game.entities.players.PlayerId;

public class Notification {
    private final PlayerId playerId;
    private final NotificationType type;

    public Notification(PlayerId playerId, NotificationType type) {
        this.playerId = playerId;
        this.type = type;
    }

    public PlayerId getPlayerId() {
        return playerId;
    }

    public NotificationType getType() {
        return type;
    }

    public enum NotificationType {PLAYER_HAS_DIED}
}
