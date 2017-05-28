package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.tasks.SavedTask;
import com.davwards.elementals.game.tasks.TaskId;
import com.davwards.elementals.game.tasks.UnsavedTask;

public interface TaskRepository extends
        CrudRepository<UnsavedTask,SavedTask,TaskId> {
}
