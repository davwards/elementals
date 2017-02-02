package com.davwards.elementals.game.entities.tasks;

import com.davwards.elementals.game.entities.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends CrudRepository<UnsavedTask, SavedTask, TaskId> {
    List<SavedTask> all();

    SavedTask save(UnsavedTask unsavedTask);

    Optional<SavedTask> find(TaskId id);

    SavedTask update(SavedTask savedTask);
}
