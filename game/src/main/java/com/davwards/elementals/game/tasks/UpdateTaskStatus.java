package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.players.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.PlayerRepository;

import java.time.LocalDateTime;

public class UpdateTaskStatus {
    private final TaskRepository taskRepository;
    private final PlayerRepository playerRepository;

    public UpdateTaskStatus(TaskRepository taskRepository,
                            PlayerRepository playerRepository) {
        this.taskRepository = taskRepository;
        this.playerRepository = playerRepository;
    }

    public interface Outcome<T> {
        T noSuchTask();
        T taskExpired(SavedTask updatedTask);
        T noStatusChange(SavedTask task);
    }

    public <T> T perform(TaskId id, LocalDateTime currentTime, Outcome<T> handle) throws NoSuchTaskException {
        return taskRepository.find(id)
                .map(task -> {
                    if (taskIsPastDue(task, currentTime) && task.isIncomplete()) {
                        ImmutableSavedTask updatedTask = ImmutableSavedTask.copyOf(task)
                                .withStatus(Task.Status.PAST_DUE);

                        taskRepository.update(updatedTask);

                        playerRepository.find(task.playerId())
                                .ifPresent(player -> {
                                    playerRepository.update(ImmutableSavedPlayer
                                            .copyOf(player)
                                            .withHealth(player.health() - GameConstants.EXPIRED_TASK_PENALTY)
                                    );
                                });
                        return handle.taskExpired(updatedTask);
                    } else {
                        return handle.noStatusChange(task);
                    }
                })
                .orElseGet(handle::noSuchTask);
    }

    private static boolean taskIsPastDue(Task task, LocalDateTime currentTime) {
        return task.deadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }
}
