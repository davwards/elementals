package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.players.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.time.LocalDateTime;

public class UpdateTaskStatus {
    public interface Outcome<T> {
        T noSuchTask();

        T taskExpired(SavedTask updatedTask);

        T noStatusChange(SavedTask task);
    }

    public <T> T perform(TaskId id,
                         LocalDateTime currentTime,
                         Outcome<T> handle) {

        return taskRepository.find(id)
                .map(task -> (taskIsPastDue(task, currentTime) && task.isIncomplete())
                        ? handle.taskExpired(updatePlayerAndTask(task))
                        : handle.noStatusChange(task))
                .orElseGet(handle::noSuchTask);
    }

    private final TaskRepository taskRepository;

    private final PlayerRepository playerRepository;

    public UpdateTaskStatus(TaskRepository taskRepository,
                            PlayerRepository playerRepository) {
        this.taskRepository = taskRepository;
        this.playerRepository = playerRepository;
    }

    private static boolean taskIsPastDue(Task task, LocalDateTime currentTime) {
        return task.deadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }

    private ImmutableSavedTask updatePlayerAndTask(SavedTask task) {
        ImmutableSavedTask updatedTask = ImmutableSavedTask.copyOf(task)
                .withStatus(Task.Status.PAST_DUE);

        taskRepository.update(updatedTask);

        playerRepository.find(task.playerId())
                .ifPresent(player -> playerRepository
                        .update(ImmutableSavedPlayer
                                .copyOf(player)
                                .withHealth(player.health() - GameConstants.EXPIRED_TASK_PENALTY)
                        )
                );
        return updatedTask;
    }
}
