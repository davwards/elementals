package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.support.persistence.CrudRepository;
import com.davwards.elementals.game.tasks.models.RecurringTaskId;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.UnsavedRecurringTask;

public interface RecurringTaskRepository extends
        CrudRepository<UnsavedRecurringTask, SavedRecurringTask, RecurringTaskId> {
}
