package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.tasks.TaskRepository;

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
            player.addExperience(GameConstants.TASK_COMPLETION_PRIZE);
            playerRepository.update(player);
        });
        return taskRepository.update(ImmutableSavedTask.builder()
                .from(task)
                .status(Task.Status.COMPLETE)
                .build()
        );
    }
}
