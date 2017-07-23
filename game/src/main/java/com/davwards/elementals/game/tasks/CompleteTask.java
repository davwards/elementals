package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.players.UpdatePlayerCurrencies;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.Task;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import static com.davwards.elementals.game.GameConstants.TASK_COMPLETION_COIN_PRIZE;
import static com.davwards.elementals.game.GameConstants.TASK_COMPLETION_EXPERIENCE_PRIZE;
import static com.davwards.elementals.support.language.StrictOptional.strict;

public class CompleteTask {
    public interface Outcome<T> {
        T taskSuccessfullyCompleted(SavedTask completedTask);

        T noSuchTask();
    }

    public <T> T perform(TaskId id, Outcome<T> outcome) {
        return strict(taskRepository.find(id))
                .map(this::rewardPlayerAndMarkTaskComplete)
                .map(outcome::taskSuccessfullyCompleted)
                .orElseGet(outcome::noSuchTask);
    }

    private SavedTask rewardPlayerAndMarkTaskComplete(SavedTask task) {
        rewardPlayer(task);
        return taskRepository
                .update(SavedTask.copy(task).withStatus(Task.Status.COMPLETE));
    }

    private void rewardPlayer(final SavedTask task) {
        updatePlayerCurrencies.perform(
                task.playerId(),
                new UpdatePlayerCurrencies.CurrencyChanges()
                        .experience(TASK_COMPLETION_EXPERIENCE_PRIZE)
                        .coin(TASK_COMPLETION_COIN_PRIZE),
                new UpdatePlayerCurrencies.Outcome<SavedPlayer>() {
                    @Override
                    public SavedPlayer updatedPlayer(SavedPlayer player) {
                        return player;
                    }

                    @Override
                    public SavedPlayer noSuchPlayer() {
                        throw new RuntimeException(
                                "Encountered invalid state: task #" + task.getId() +
                                        " has playerId " + task.playerId() +
                                        " which does not correspond to a player record");
                    }
                });
    }

    private final TaskRepository taskRepository;
    private final UpdatePlayerCurrencies updatePlayerCurrencies;

    public CompleteTask(TaskRepository taskRepository, UpdatePlayerCurrencies updatePlayerCurrencies) {
        this.taskRepository = taskRepository;
        this.updatePlayerCurrencies = updatePlayerCurrencies;
    }
}
