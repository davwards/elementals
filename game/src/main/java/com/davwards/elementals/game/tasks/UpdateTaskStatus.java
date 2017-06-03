package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.Task;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.time.LocalDateTime;

import static com.davwards.elementals.game.support.language.StrictOptional.strict;

public class UpdateTaskStatus {
    public interface Outcome<T> {
        T noSuchTask();

        T taskExpired(SavedTask updatedTask);

        T noStatusChange(SavedTask task);
    }

    public <T> T perform(TaskId id,
                         LocalDateTime currentTime,
                         Outcome<T> handle) {

        return strict(taskRepository.find(id))
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

    private SavedTask updatePlayerAndTask(SavedTask task) {
        playerRepository
                .find(task.playerId())
                .ifPresent(this::damagePlayer);

        return taskRepository.update(
                SavedTask.copy(task).withStatus(Task.Status.PAST_DUE)
        );
    }

    private SavedPlayer damagePlayer(SavedPlayer player) {
        return playerRepository.update(
                SavedPlayer.copy(player).withHealth(
                        player.health() - GameConstants.EXPIRED_TASK_PENALTY
                )
        );
    }
}
