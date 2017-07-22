package com.davwards.elementals.game.notification;

import com.davwards.elementals.game.players.models.PlayerId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (!playerId.equals(that.playerId)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = playerId.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public enum NotificationType {PLAYER_HAS_DIED}
}
