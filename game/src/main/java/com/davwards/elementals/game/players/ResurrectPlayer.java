package com.davwards.elementals.game.players;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.exceptions.NoSuchPlayerException;
import com.davwards.elementals.game.notification.Notifier;
import com.davwards.elementals.game.players.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.PlayerId;
import com.davwards.elementals.game.players.PlayerRepository;
import com.davwards.elementals.game.players.SavedPlayer;

import static com.davwards.elementals.game.notification.Notification.NotificationType.PLAYER_HAS_DIED;

public class ResurrectPlayer {
    private final PlayerRepository playerRepository;
    private final Notifier notifier;

    public ResurrectPlayer(PlayerRepository playerRepository, Notifier notifier) {
        this.playerRepository = playerRepository;
        this.notifier = notifier;
    }

    public void perform(PlayerId id) {
        SavedPlayer player = playerRepository.find(id)
                .orElseThrow(() -> new NoSuchPlayerException(id));

        if(!player.isAlive()) {
            playerRepository.update(ImmutableSavedPlayer.copyOf(player)
                    .withHealth(GameConstants.STARTING_HEALTH)
                    .withExperience(0));
            notifier.sendNotification(id, PLAYER_HAS_DIED);
        }
    }
}
