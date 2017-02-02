package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.TaskId;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;

public class FetchTask {
    private final TaskRepository taskRepository;

    public FetchTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public SavedTask perform(TaskId id) {
        return taskRepository.find(id).orElseThrow(() -> new NoSuchTaskException(id));
    }
}
