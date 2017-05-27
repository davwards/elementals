package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.entities.CrudRepository;

public interface TaskRepository extends
        CrudRepository<UnsavedTask,SavedTask,TaskId> {
}
