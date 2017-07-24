package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.players.UpdatePlayerCurrencies;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.Task;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.time.LocalDateTime;

import static com.davwards.elementals.game.GameConstants.EXPIRED_TASK_PENALTY;
import static com.davwards.elementals.support.language.StrictOptional.strict;

public class UpdateTaskStatus {
    public interface Outcome<T> {
        T noSuchTask();

        T taskExpired(SavedTask updatedTask);

        T noStatusChange(SavedTask task);
    }

    public <T> T perform(TaskId id,
                         LocalDateTime currentTime,
                         Outcome<T> outcome) {

        return strict(taskRepository.find(id))
                .map(task -> (taskIsPastDue(task, currentTime) && task.isIncomplete())
                        ? outcome.taskExpired(updatePlayerAndTask(task))
                        : outcome.noStatusChange(task))
                .orElseGet(outcome::noSuchTask);
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
        new UpdatePlayerCurrencies(playerRepository).perform(
                task.playerId(),
                new UpdatePlayerCurrencies.CurrencyChanges()
                        .health(EXPIRED_TASK_PENALTY),
                new UpdatePlayerCurrencies.Outcome<Void>() {
                    @Override
                    public Void updatedPlayer(SavedPlayer player) {
                        return null;
                    }

                    @Override
                    public Void noSuchPlayer() {
                        throw new RuntimeException("Task #" + task.getId() +
                                " referenced nonexistent player #" + task.playerId() + "!");
                    }
                }
        );

        return taskRepository.update(
                SavedTask.copy(task).withStatus(Task.Status.PAST_DUE)
        );
    }
}
