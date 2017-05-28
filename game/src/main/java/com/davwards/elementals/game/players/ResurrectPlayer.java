package com.davwards.elementals.game.players;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.exceptions.NoSuchPlayerException;
import com.davwards.elementals.game.notification.Notifier;

import static com.davwards.elementals.game.notification.Notification.NotificationType.PLAYER_HAS_DIED;

public class ResurrectPlayer {
    private final PlayerRepository playerRepository;
    private final Notifier notifier;

    public ResurrectPlayer(PlayerRepository playerRepository, Notifier notifier) {
        this.playerRepository = playerRepository;
        this.notifier = notifier;
    }

    public interface Outcome<T> {
        T noSuchPlayer();
        T playerWasResurrected(SavedPlayer updatedPlayer);
        T playerDidNotNeedToBeResurrected(SavedPlayer player);
    }

    public <T> T perform(PlayerId id, Outcome<T> handle) {
        return playerRepository.find(id)
                .map(player -> {
                    if(!player.isAlive()) {
                        SavedPlayer updatedPlayer = ImmutableSavedPlayer.copyOf(player)
                                .withHealth(GameConstants.STARTING_HEALTH)
                                .withExperience(0);
                        playerRepository.update(updatedPlayer);
                        notifier.sendNotification(id, PLAYER_HAS_DIED);

                        return handle.playerWasResurrected(updatedPlayer);
                    } else {
                        return handle.playerDidNotNeedToBeResurrected(player);
                    }
                })
                .orElseGet(handle::noSuchPlayer);
    }
}
