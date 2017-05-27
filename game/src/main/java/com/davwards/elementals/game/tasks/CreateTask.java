package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.players.PlayerId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

public class CreateTask {
    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public interface Outcome<T> {
        T successfullyCreatedTask(SavedTask createdTask);
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         Outcome<T> handle) {

        return perform(playerId, title, Optional.empty(), handle);
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         LocalDateTime deadline,
                         Outcome<T> handle) {

        return perform(playerId, title, Optional.of(deadline), handle);
    }

    private <T> T perform(PlayerId playerId, String title, Optional<LocalDateTime> deadline, Outcome<T> handle) {
        return handle.successfullyCreatedTask(
                taskRepository.save(ImmutableUnsavedTask.builder()
                        .playerId(playerId)
                        .title(title)
                        .status(Task.Status.INCOMPLETE)
                        .deadline(deadline)
                        .build())
        );
    }
}
