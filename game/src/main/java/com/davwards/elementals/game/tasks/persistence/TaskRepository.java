package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepository;
import com.davwards.elementals.game.tasks.models.RecurringTaskId;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.models.UnsavedTask;

import java.util.List;

public interface TaskRepository extends
        CrudRepository<UnsavedTask,SavedTask,TaskId> {

    List<SavedTask> findByPlayerId(PlayerId playerId);

    List<SavedTask> findByParentRecurringTaskId(RecurringTaskId recurringTaskId);
}
