package com.davwards.elementals.tasks;

import com.davwards.elementals.players.UpdatePlayerCurrencies;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.models.Task;
import com.davwards.elementals.tasks.models.TaskId;
import com.davwards.elementals.tasks.persistence.TaskRepository;

import java.util.function.Function;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class CompleteTask {

    public interface Outcome<T> {
        T taskSuccessfullyCompleted(SavedTask completedTask);
        T noSuchTask();
    }

    public <T> T perform(TaskId id, Outcome<T> outcome) {
        return strict(taskRepository.find(id))
                .map(rewardPlayerAndMarkTaskComplete(outcome))
                .orElseGet(outcome::noSuchTask);
    }

    private <T> Function<SavedTask, T> rewardPlayerAndMarkTaskComplete(Outcome<T> outcome) {
        return task -> {
            rewardPlayer(task);
            return outcome.taskSuccessfullyCompleted(
                    taskRepository.update(
                            SavedTask.copy(task)
                                    .withStatus(Task.Status.COMPLETE)
                    )
            );
        };
    }

    private void rewardPlayer(final SavedTask task) {

        updatePlayerCurrencies.perform(
                task.playerId(),
                new UpdatePlayerCurrencies.CurrencyChanges()
                        .experience(TaskGameConstants.TASK_COMPLETION_EXPERIENCE_PRIZE)
                        .coin(TaskGameConstants.TASK_COMPLETION_COIN_PRIZE),
                failIfPlayerDoesNotExist(task)
        );
    }

    private UpdatePlayerCurrencies.Outcome<Void> failIfPlayerDoesNotExist(final SavedTask task) {
        return new UpdatePlayerCurrencies.Outcome<Void>() {
            @Override
            public Void updatedPlayer(SavedPlayer player) {
                return null;
            }

            @Override
            public Void noSuchPlayer() {
                throw new RuntimeException(
                        "Encountered invalid state: task #" + task.getId() +
                                " has playerId " + task.playerId() +
                                " which does not correspond to a player record");
            }
        };
    }

    private final TaskRepository taskRepository;
    private final UpdatePlayerCurrencies updatePlayerCurrencies;

    public CompleteTask(TaskRepository taskRepository, UpdatePlayerCurrencies updatePlayerCurrencies) {
        this.taskRepository = taskRepository;
        this.updatePlayerCurrencies = updatePlayerCurrencies;
    }
}
