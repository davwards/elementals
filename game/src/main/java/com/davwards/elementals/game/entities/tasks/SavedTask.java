package com.davwards.elementals.game.entities.tasks;

import com.davwards.elementals.game.entities.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedTask extends Task, SavedEntity<TaskId> {
}
