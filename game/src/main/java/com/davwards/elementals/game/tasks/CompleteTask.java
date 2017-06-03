package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.Task;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import static com.davwards.elementals.game.support.language.StrictOptional.strict;

public class CompleteTask {
    public interface Outcome<T> {
        T taskSuccessfullyCompleted(SavedTask completedTask);

        T noSuchTask();
    }

    public <T> T perform(TaskId id, Outcome<T> handle) {
        return strict(taskRepository.find(id))
                .map(this::markTaskCompletedAndAwardExperienceToPlayer)
                .map(handle::taskSuccessfullyCompleted)
                .orElseGet(handle::noSuchTask);
    }

    private SavedTask markTaskCompletedAndAwardExperienceToPlayer(SavedTask task) {
        playerRepository
                .find(task.playerId())
                .ifPresent(player -> playerRepository.update(
                        SavedPlayer.copy(player).withExperience(
                                player.experience() + GameConstants.TASK_COMPLETION_PRIZE
                        )
                ));

        return taskRepository
                .update(SavedTask.copy(task)
                        .withStatus(Task.Status.COMPLETE)
                );
    }

    private final TaskRepository taskRepository;
    private final PlayerRepository playerRepository;

    public CompleteTask(TaskRepository taskRepository, PlayerRepository playerRepository) {
        this.taskRepository = taskRepository;
        this.playerRepository = playerRepository;
    }
}
