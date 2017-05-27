package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.players.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.PlayerRepository;

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
                        playerRepository.update(ImmutableSavedPlayer
                                .copyOf(player)
                                .withHealth(player.health() - GameConstants.EXPIRED_TASK_PENALTY)
                        );
                    });
        }
    }

    private static boolean taskIsPastDue(Task task, LocalDateTime currentTime) {
        return task.deadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }
}
