package com.davwards.elementals.players.resurrectionscheduler;

import com.davwards.elementals.players.ResurrectPlayer;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;
import com.davwards.elementals.support.scheduling.TimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class ResurrectionScheduler {
    private final PlayerRepository playerRepository;
    private final ResurrectPlayer resurrectPlayer;
    private final TimeProvider timeProvider;
    private static final Logger logger = LoggerFactory.getLogger(ResurrectionScheduler.class);

    public ResurrectionScheduler(PlayerRepository playerRepository, ResurrectPlayer resurrectPlayer, TimeProvider timeProvider) {
        this.playerRepository = playerRepository;
        this.resurrectPlayer = resurrectPlayer;
        this.timeProvider = timeProvider;
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
