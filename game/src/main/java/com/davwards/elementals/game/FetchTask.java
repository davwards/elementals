package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.TaskId;
import com.davwards.elementals.game.entities.tasks.TaskRepository;

import java.util.function.Function;
import java.util.function.Supplier;

public class FetchTask {
    private final TaskRepository taskRepository;

    public FetchTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public <T> T perform(TaskId id, Function<SavedTask, T> fetchedTask, Supplier<T> noSuchTask) {
        return taskRepository
                .find(id)
                .map(fetchedTask)
                .orElseGet(noSuchTask);
    }
}
