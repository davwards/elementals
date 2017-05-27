package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.players.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.PlayerRepository;

public class CompleteTask {
    private final TaskRepository taskRepository;
    private final PlayerRepository playerRepository;

    public CompleteTask(TaskRepository taskRepository, PlayerRepository playerRepository) {
        this.taskRepository = taskRepository;
        this.playerRepository = playerRepository;
    }

    public interface Outcome<T> {
        T taskSuccessfullyCompleted(SavedTask completedTask);
        T noSuchTask();
    }

    public <T> T perform(TaskId id,
                         Outcome<T> handle) {

        return taskRepository.find(id)
                .map(this::updateTaskStatusAndAwardExperienceToPlayer)
                .map(handle::taskSuccessfullyCompleted)
                .orElseGet(handle::noSuchTask);
    }

    private SavedTask updateTaskStatusAndAwardExperienceToPlayer(SavedTask task) {
        playerRepository.find(task.playerId()).ifPresent(player -> {
            playerRepository.update(ImmutableSavedPlayer
                    .copyOf(player)
                    .withExperience(player.experience() + GameConstants.TASK_COMPLETION_PRIZE)
            );
        });
        return taskRepository.update(ImmutableSavedTask.builder()
                .from(task)
                .status(Task.Status.COMPLETE)
                .build()
        );
    }
}
