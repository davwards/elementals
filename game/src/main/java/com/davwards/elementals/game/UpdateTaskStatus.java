package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.Task;
import com.davwards.elementals.game.entities.tasks.TaskId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;

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

        if(taskIsPastDue(task, currentTime) && task.isIncomplete()) {
            task.setStatus(Task.Status.PAST_DUE);
            taskRepository.update(task);
            playerRepository.find(task.getPlayerId())
                    .ifPresent(player -> {
                        player.decreaseHealth(GameConstants.EXPIRED_TASK_PENALTY);
                        playerRepository.update(player);
                    });
        }
    }

    private static boolean taskIsPastDue(Task task, LocalDateTime currentTime) {
        return task.getDeadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }
}
