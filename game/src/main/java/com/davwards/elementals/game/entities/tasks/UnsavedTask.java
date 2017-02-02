package com.davwards.elementals.game.entities.tasks;

import com.davwards.elementals.game.entities.players.PlayerId;

import java.time.LocalDateTime;

public class UnsavedTask extends Task {
    public UnsavedTask(PlayerId playerId, String title, Status status) {
        super(playerId, title, status);
    }

    public UnsavedTask(PlayerId playerId, String title, Status status, LocalDateTime deadline) {
        super(playerId, title, status, deadline);
    }
}
