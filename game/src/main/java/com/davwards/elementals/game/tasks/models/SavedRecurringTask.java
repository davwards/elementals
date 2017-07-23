package com.davwards.elementals.game.tasks.models;

import com.davwards.elementals.support.persistence.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedRecurringTask extends RecurringTask, SavedEntity<RecurringTaskId> {
    static ImmutableSavedRecurringTask copy(SavedRecurringTask original) {
        return ImmutableSavedRecurringTask.copyOf(original);
    }
}
