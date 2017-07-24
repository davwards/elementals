package com.davwards.elementals.tasks.persistence;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepository;
import com.davwards.elementals.tasks.models.RecurringTaskId;
import com.davwards.elementals.tasks.models.SavedRecurringTask;
import com.davwards.elementals.tasks.models.UnsavedRecurringTask;

import java.util.List;

public interface RecurringTaskRepository extends
        CrudRepository<UnsavedRecurringTask, SavedRecurringTask, RecurringTaskId> {
    List<SavedRecurringTask> findByPlayerId(PlayerId matchingPlayerId);
}
