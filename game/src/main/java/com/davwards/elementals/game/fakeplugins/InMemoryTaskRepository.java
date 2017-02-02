package com.davwards.elementals.game.fakeplugins;

import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.TaskId;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import com.davwards.elementals.game.entities.tasks.UnsavedTask;

import java.util.*;

public class InMemoryTaskRepository implements TaskRepository {
    private Map<TaskId, SavedTask> contents = new HashMap<>();

    @Override
    public List<SavedTask> all() {
        return new ArrayList<>(contents.values());
    }

    @Override
    public SavedTask save(UnsavedTask unsavedTask) {
        TaskId id = new TaskId(UUID.randomUUID().toString());

        SavedTask savedTask = new SavedTask(
                id,
                unsavedTask.getPlayerId(),
                unsavedTask.getTitle(),
                unsavedTask.getStatus(),
                unsavedTask.getDeadline()
        );

        contents.put(id, savedTask);
        return SavedTask.clone(savedTask);
    }

    @Override
    public Optional<SavedTask> find(TaskId id) {
        return Optional.ofNullable(contents.get(id))
                .map(SavedTask::clone);
    }

    @Override
    public SavedTask update(SavedTask savedTask) {
        contents.put(savedTask.getId(), SavedTask.clone(savedTask));
        return savedTask;
    }
}
