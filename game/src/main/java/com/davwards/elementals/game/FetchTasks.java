package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.TaskRepository;

import java.util.List;
import java.util.function.Function;

public class FetchTasks {
    private final TaskRepository taskRepository;

    public FetchTasks(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public <T> T perform(Function<List<SavedTask>, T> foundTasks) {
        return foundTasks.apply(taskRepository.all());
    }
}
