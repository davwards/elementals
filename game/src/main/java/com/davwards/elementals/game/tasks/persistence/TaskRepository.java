package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.support.persistence.CrudRepository;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.models.UnsavedTask;

public interface TaskRepository extends
        CrudRepository<UnsavedTask,SavedTask,TaskId> {
}
