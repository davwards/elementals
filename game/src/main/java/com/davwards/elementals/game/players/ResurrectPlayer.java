package com.davwards.elementals.game.players;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.notification.Notifier;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

import static com.davwards.elementals.game.notification.Notification.NotificationType.PLAYER_HAS_DIED;
import static com.davwards.elementals.game.support.language.StrictOptional.strict;

public class ResurrectPlayer {
    public interface Outcome<T> {
        T noSuchPlayer();
        T playerWasResurrected(SavedPlayer updatedPlayer);
        T playerDidNotNeedToBeResurrected(SavedPlayer player);
    }

    public <T> T perform(PlayerId id, Outcome<T> handle) {
        return strict(playerRepository.find(id))
                .map(player -> player.isAlive()
                        ? handle.playerDidNotNeedToBeResurrected(player)
                        : handle.playerWasResurrected(updateDeadPlayer(id, player)))
                .orElseGet(handle::noSuchPlayer);
    }

    private SavedPlayer updateDeadPlayer(PlayerId id, SavedPlayer player) {
        notifier.sendNotification(id, PLAYER_HAS_DIED);
        return playerRepository.update(
                SavedPlayer.copy(player)
                        .withHealth(GameConstants.STARTING_HEALTH)
                        .withExperience(0)
                        .withCoin(0)
        );
    }

    private final PlayerRepository playerRepository;
    private final Notifier notifier;

    public ResurrectPlayer(PlayerRepository playerRepository, Notifier notifier) {
        this.playerRepository = playerRepository;
        this.notifier = notifier;
    }
}
