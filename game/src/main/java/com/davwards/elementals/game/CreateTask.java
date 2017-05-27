package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.tasks.TaskRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

public class CreateTask {
    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         Function<SavedTask, T> createdTask) {

        return perform(playerId, title, Optional.empty(), createdTask);
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         LocalDateTime deadline,
                         Function<SavedTask, T> createdTask) {

        return perform(playerId, title, Optional.of(deadline), createdTask);
    }

    private <T> T perform(PlayerId playerId, String title, Optional<LocalDateTime> deadline, Function<SavedTask, T> createdTask) {
        return createdTask.apply(
                taskRepository.save(ImmutableUnsavedTask.builder()
                        .playerId(playerId)
                        .title(title)
                        .status(Task.Status.INCOMPLETE)
                        .deadline(deadline)
                        .build())
        );
    }
}
