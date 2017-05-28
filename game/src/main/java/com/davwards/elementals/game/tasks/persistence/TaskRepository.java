package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.models.UnsavedTask;

public interface TaskRepository extends
        CrudRepository<UnsavedTask,SavedTask,TaskId> {
}
