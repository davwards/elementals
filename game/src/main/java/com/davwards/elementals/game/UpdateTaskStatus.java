package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.tasks.TaskRepository;

import java.time.LocalDateTime;

public class UpdateTaskStatus {
    private final TaskRepository taskRepository;
    private final PlayerRepository playerRepository;

    public UpdateTaskStatus(TaskRepository taskRepository,
                            PlayerRepository playerRepository) {
        this.taskRepository = taskRepository;
        this.playerRepository = playerRepository;
    }

    public void perform(TaskId id, LocalDateTime currentTime) throws NoSuchTaskException {
        SavedTask task = taskRepository.find(id)
                .orElseThrow(() -> new NoSuchTaskException(id));

        if (taskIsPastDue(task, currentTime) && task.isIncomplete()) {

            taskRepository
                    .update(ImmutableSavedTask.copyOf(task)
                            .withStatus(Task.Status.PAST_DUE));

            playerRepository.find(task.playerId())
                    .ifPresent(player -> {
                        player.decreaseHealth(GameConstants.EXPIRED_TASK_PENALTY);
                        playerRepository.update(player);
                    });
        }
    }

    private static boolean taskIsPastDue(Task task, LocalDateTime currentTime) {
        return task.deadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }
}
