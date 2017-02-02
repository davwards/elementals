package com.davwards.elementals.game.entities.tasks;

import com.davwards.elementals.game.entities.SavedEntity;
import com.davwards.elementals.game.entities.players.PlayerId;

import java.time.LocalDateTime;
import java.util.Optional;

public class SavedTask extends Task implements SavedEntity<TaskId> {
    private final TaskId id;

    public SavedTask(TaskId id,
                     PlayerId playerId,
                     String title,
                     Status status,
                     Optional<LocalDateTime> deadline) {

        super(playerId, title, status, deadline);
        this.id = id;
    }

    public TaskId getId() {
        return id;
    }

    public static SavedTask clone(SavedTask savedTask) {
        return new SavedTask(
                savedTask.getId(),
                savedTask.getPlayerId(),
                savedTask.getTitle(),
                savedTask.getStatus(),
                savedTask.getDeadline()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedTask savedTask = (SavedTask) o;

        return id.equals(savedTask.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
