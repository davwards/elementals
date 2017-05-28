package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.util.List;

public class FetchTasks {
    public interface Outcome<T> {
        T foundTasks(List<SavedTask> tasks);
    }

    public <T> T perform(Outcome<T> handle) {
        return handle.foundTasks(taskRepository.all());
    }

    private final TaskRepository taskRepository;

    public FetchTasks(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
