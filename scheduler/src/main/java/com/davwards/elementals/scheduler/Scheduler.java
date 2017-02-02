package com.davwards.elementals.scheduler;

import com.davwards.elementals.game.ResurrectPlayer;
import com.davwards.elementals.game.UpdateTaskStatus;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;

public class Scheduler {
    private final TimeProvider timeProvider;
    private final UpdateTaskStatus updateTaskStatus;
    private final ResurrectPlayer resurrectPlayer;
    private final PlayerRepository playerRepository;
    private final TaskRepository taskRepository;

    public Scheduler(TimeProvider timeProvider,
                     UpdateTaskStatus updateTaskStatus,
                     ResurrectPlayer resurrectPlayer,
                     PlayerRepository playerRepository,
                     TaskRepository taskRepository) {
        this.timeProvider = timeProvider;
        this.updateTaskStatus = updateTaskStatus;
        this.resurrectPlayer = resurrectPlayer;
        this.playerRepository = playerRepository;
        this.taskRepository = taskRepository;
    }

    @Scheduled(fixedDelay = 1000)
    public void updateTasks() {
        taskRepository.all().forEach(task -> updateTaskStatus.perform(task.getId(), timeProvider.currentTime()));
    }

    @Scheduled(fixedDelay = 1000)
    public void resurrectDeadPlayers() {
        playerRepository.all().forEach(player -> resurrectPlayer.perform(player.getId()));
    }
}
