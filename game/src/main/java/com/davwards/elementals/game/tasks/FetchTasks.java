package com.davwards.elementals.game.tasks;

import java.util.List;
import java.util.function.Function;

public class FetchTasks {
    private final TaskRepository taskRepository;

    public FetchTasks(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public interface Outcome<T> {
        T foundTasks(List<SavedTask> tasks);
    }

    public <T> T perform(Outcome<T> handle) {
        return handle.foundTasks(taskRepository.all());
    }
}
