package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.TaskRepository;

import java.util.List;

public class FetchTasks {
    private final TaskRepository taskRepository;

    public FetchTasks(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<SavedTask> perform() {
        return taskRepository.all();
    }
}
