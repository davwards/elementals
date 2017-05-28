package com.davwards.elementals.game.players;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.notification.Notifier;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

import static com.davwards.elementals.game.notification.Notification.NotificationType.PLAYER_HAS_DIED;

public class ResurrectPlayer {
    public interface Outcome<T> {
        T noSuchPlayer();
        T playerWasResurrected(SavedPlayer updatedPlayer);
        T playerDidNotNeedToBeResurrected(SavedPlayer player);
    }

    public <T> T perform(PlayerId id, Outcome<T> handle) {
        return playerRepository.find(id)
                .map(player -> player.isAlive()
                        ? handle.playerDidNotNeedToBeResurrected(player)
                        : handle.playerWasResurrected(updateDeadPlayer(id, player)))
                .orElseGet(handle::noSuchPlayer);
    }

    private SavedPlayer updateDeadPlayer(PlayerId id, SavedPlayer player) {
        SavedPlayer updatedPlayer = ImmutableSavedPlayer.copyOf(player)
                .withHealth(GameConstants.STARTING_HEALTH)
                .withExperience(0);
        playerRepository.update(updatedPlayer);
        notifier.sendNotification(id, PLAYER_HAS_DIED);
        return updatedPlayer;
    }

    private final PlayerRepository playerRepository;
    private final Notifier notifier;

    public ResurrectPlayer(PlayerRepository playerRepository, Notifier notifier) {
        this.playerRepository = playerRepository;
        this.notifier = notifier;
    }
}
