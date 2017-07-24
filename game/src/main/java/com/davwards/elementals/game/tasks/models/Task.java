package com.davwards.elementals.game.tasks.models;

import com.davwards.elementals.players.models.PlayerId;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Task {
    String title();
    Status status();
    Optional<LocalDateTime> deadline();
    PlayerId playerId();
    Optional<RecurringTaskId> parentRecurringTaskId();

    default Boolean isComplete() {
        return this.status().equals(Status.COMPLETE);
    }

    default Boolean isIncomplete() {
        return this.status().equals(Status.INCOMPLETE);
    }

    default Boolean isInstanceOfRecurringTask() {
        return this.parentRecurringTaskId().isPresent();
    }

    enum Status {COMPLETE, PAST_DUE, INCOMPLETE}
}
