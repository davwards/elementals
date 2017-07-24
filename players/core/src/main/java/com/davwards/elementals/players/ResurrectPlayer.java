package com.davwards.elementals.players;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.notification.Notifier;
import com.davwards.elementals.players.persistence.PlayerRepository;

import static com.davwards.elementals.players.notification.Notification.NotificationType.PLAYER_HAS_DIED;
import static com.davwards.elementals.support.language.StrictOptional.strict;

public class ResurrectPlayer {
    public interface Outcome<T> {
        T noSuchPlayer();
        T playerWasResurrected(SavedPlayer updatedPlayer);
        T playerDidNotNeedToBeResurrected(SavedPlayer player);
    }

    public <T> T perform(PlayerId id, Outcome<T> outcome) {
        return strict(playerRepository.find(id))
                .map(player -> player.isAlive()
                        ? outcome.playerDidNotNeedToBeResurrected(player)
                        : outcome.playerWasResurrected(updateDeadPlayer(id, player)))
                .orElseGet(outcome::noSuchPlayer);
    }

    private SavedPlayer updateDeadPlayer(PlayerId id, SavedPlayer player) {
        notifier.sendNotification(id, PLAYER_HAS_DIED);
        return playerRepository.update(
                SavedPlayer.copy(player)
                        .withHealth(PlayerGameConstants.STARTING_HEALTH)
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
