package com.davwards.elementals.tasks.persistence;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepository;
import com.davwards.elementals.tasks.models.RecurringTaskId;
import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.models.TaskId;
import com.davwards.elementals.tasks.models.UnsavedTask;

import java.util.List;

public interface TaskRepository extends
        CrudRepository<UnsavedTask,SavedTask,TaskId> {

    List<SavedTask> findByPlayerId(PlayerId playerId);

    List<SavedTask> findByParentRecurringTaskId(RecurringTaskId recurringTaskId);
}
