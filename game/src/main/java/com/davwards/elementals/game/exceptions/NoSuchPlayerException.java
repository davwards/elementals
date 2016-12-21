package com.davwards.elementals.game.exceptions;

import com.davwards.elementals.game.entities.players.PlayerId;

public class NoSuchPlayerException extends RuntimeException {
    private PlayerId playerId;

    public NoSuchPlayerException(PlayerId playerId) {
        this.playerId = playerId;
    }

    public PlayerId getPlayerId() {
        return playerId;
    }
}
