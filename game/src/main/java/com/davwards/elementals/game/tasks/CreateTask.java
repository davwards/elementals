package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.Task;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class CreateTask {
    public interface Outcome<T> {
        T successfullyCreatedTask(SavedTask createdTask);
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         Outcome<T> outcome) {

        return perform(playerId, title, Optional.empty(), outcome);
    }

    public <T> T perform(PlayerId playerId,
                         String title,
                         LocalDateTime deadline,
                         Outcome<T> outcome) {

        return perform(playerId, title, Optional.of(deadline), outcome);
    }

    private <T> T perform(
            PlayerId playerId,
            String title,
            Optional<LocalDateTime> deadline,
            Outcome<T> outcome) {

        return outcome.successfullyCreatedTask(
                taskRepository.save(ImmutableUnsavedTask.builder()
                        .playerId(playerId)
                        .title(title)
                        .status(Task.Status.INCOMPLETE)
                        .deadline(deadline)
                        .build())
        );
    }

    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
