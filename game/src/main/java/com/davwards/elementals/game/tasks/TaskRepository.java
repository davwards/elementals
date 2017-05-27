package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.UnsavedTask;
import com.davwards.elementals.game.entities.tasks.TaskId;

public interface TaskRepository extends
        CrudRepository<UnsavedTask,SavedTask,TaskId> {
}
