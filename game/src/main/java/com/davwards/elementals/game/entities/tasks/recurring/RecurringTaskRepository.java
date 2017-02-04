package com.davwards.elementals.game.entities.tasks.recurring;

import com.davwards.elementals.game.entities.CrudRepository;

public interface RecurringTaskRepository extends CrudRepository<UnsavedRecurringTask, SavedRecurringTask, RecurringTaskId> {
}
