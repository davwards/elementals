package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.Task;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import com.davwards.elementals.game.entities.tasks.UnsavedTask;

import java.time.LocalDateTime;
import java.util.function.Function;

public class CreateTask {
    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         Function<SavedTask, T> createdTask) {

        return createdTask.apply(
                taskRepository.save(
                        new UnsavedTask(
                                playerId,
                                title,
                                Task.Status.INCOMPLETE
                        )
                )
        );
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         LocalDateTime deadline,
                         Function<SavedTask, T> createdTask) {

        return createdTask.apply(
                taskRepository.save(
                        new UnsavedTask(
                                playerId,
                                title,
                                Task.Status.INCOMPLETE,
                                deadline
                        )
                )
        );
    }
}
