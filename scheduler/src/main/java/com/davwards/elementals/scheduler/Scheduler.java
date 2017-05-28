package com.davwards.elementals.scheduler;

import com.davwards.elementals.game.players.ResurrectPlayer;
import com.davwards.elementals.game.players.SavedPlayer;
import com.davwards.elementals.game.tasks.UpdateTaskStatus;
import com.davwards.elementals.game.players.PlayerRepository;
import com.davwards.elementals.game.tasks.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class Scheduler {
    private final TimeProvider timeProvider;
    private final UpdateTaskStatus updateTaskStatus;
    private final ResurrectPlayer resurrectPlayer;
    private final PlayerRepository playerRepository;
    private final TaskRepository taskRepository;

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

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
        playerRepository.all().forEach(player -> resurrectPlayer.perform(
                player.getId(),
                new ResurrectPlayer.Outcome<Void>() {
                    @Override
                    public Void noSuchPlayer() {
                        logger.error("Tried to check player #" + player.getId() + " for resurrection, but player did not exist");
                        return null;
                    }

                    @Override
                    public Void playerWasResurrected(SavedPlayer updatedPlayer) {
                        logger.info("Resurrected player #" + player.getId());
                        return null;
                    }

                    @Override
                    public Void playerDidNotNeedToBeResurrected(SavedPlayer player) {
                        logger.info("Considered player #" + player.getId() + " for resurrection at " + timeProvider.currentTime() + ", but player did not need to be resurrected");
                        return null;
                    }
                }
        ));
    }
}
