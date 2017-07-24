package com.davwards.elementals.tasks.models;

import com.davwards.elementals.support.persistence.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedTask extends Task, SavedEntity<TaskId> {
    static ImmutableSavedTask copy(SavedTask task) {
        return ImmutableSavedTask.copyOf(task);
    }
}
