package com.davwards.elementals.game.tasks.models;

import com.davwards.elementals.game.players.models.PlayerId;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Task {
    String title();
    Status status();
    Optional<LocalDateTime> deadline();
    PlayerId playerId();

    default Boolean isComplete() {
        return this.status().equals(Status.COMPLETE);
    }

    default Boolean isIncomplete() {
        return this.status().equals(Status.INCOMPLETE);
    }

    enum Status {COMPLETE, PAST_DUE, INCOMPLETE}
}
