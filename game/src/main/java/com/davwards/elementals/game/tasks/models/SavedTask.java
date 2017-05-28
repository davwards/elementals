package com.davwards.elementals.game.tasks.models;

import com.davwards.elementals.game.entities.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedTask extends Task, SavedEntity<TaskId> {
    static ImmutableSavedTask copy(SavedTask task) {
        return ImmutableSavedTask.copyOf(task);
    }
}
