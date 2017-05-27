package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.players.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.PlayerRepository;

import java.util.function.Function;
import java.util.function.Supplier;

public class CompleteTask {
    private final TaskRepository taskRepository;
    private final PlayerRepository playerRepository;

    public CompleteTask(TaskRepository taskRepository, PlayerRepository playerRepository) {
        this.taskRepository = taskRepository;
        this.playerRepository = playerRepository;
    }

    public <T> T perform(TaskId id,
                         Function<SavedTask, T> taskSuccessfullyCompleted,
                         Supplier<T> noSuchTask) {

        return taskRepository.find(id)
                .map(this::updateTaskStatusAndAwardExperienceToPlayer)
                .map(taskSuccessfullyCompleted)
                .orElseGet(noSuchTask);
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
