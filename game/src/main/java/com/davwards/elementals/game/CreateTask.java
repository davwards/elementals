package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.Task;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import com.davwards.elementals.game.entities.tasks.UnsavedTask;

import java.time.LocalDateTime;

public class CreateTask {
    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public SavedTask perform(PlayerId playerId, String title) {
        UnsavedTask unsavedTask = new UnsavedTask(playerId, title, Task.Status.INCOMPLETE);
        return taskRepository.save(unsavedTask);
    }

    public SavedTask perform(PlayerId playerId, String title, LocalDateTime deadline) {
        UnsavedTask unsavedTask = new UnsavedTask(playerId, title, Task.Status.INCOMPLETE, deadline);
        return taskRepository.save(unsavedTask);
    }
}
