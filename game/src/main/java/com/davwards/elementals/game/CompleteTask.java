package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.Task;
import com.davwards.elementals.game.entities.tasks.TaskId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;

public class CompleteTask {
    private final TaskRepository taskRepository;
    private final PlayerRepository playerRepository;

    public CompleteTask(TaskRepository taskRepository, PlayerRepository playerRepository) {
        this.taskRepository = taskRepository;
        this.playerRepository = playerRepository;
    }

    public SavedTask perform(TaskId id) throws NoSuchTaskException {
        SavedTask updatedTask = taskRepository.find(id)
                .map(task -> {
                    task.setStatus(Task.Status.COMPLETE);
                    return taskRepository.update(task);
                }).orElseThrow(() -> new NoSuchTaskException(id));

        playerRepository.find(updatedTask.getPlayerId()).ifPresent(player -> {
            player.addExperience(GameConstants.TASK_COMPLETION_PRIZE);
            playerRepository.update(player);
        });

        return updatedTask;
    }
}
