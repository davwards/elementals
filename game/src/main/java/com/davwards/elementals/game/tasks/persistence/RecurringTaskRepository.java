package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.CrudRepository;
import com.davwards.elementals.game.tasks.models.RecurringTaskId;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.UnsavedRecurringTask;

import java.util.List;

public interface RecurringTaskRepository extends
        CrudRepository<UnsavedRecurringTask, SavedRecurringTask, RecurringTaskId> {
    List<SavedRecurringTask> findByPlayerId(PlayerId matchingPlayerId);
}
