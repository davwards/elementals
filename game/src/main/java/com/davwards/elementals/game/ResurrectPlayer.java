package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
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

    public void perform(PlayerId id) {
        SavedPlayer player = playerRepository.find(id)
                .orElseThrow(() -> new NoSuchPlayerException(id));

        if(!player.isAlive()) {
            player.setExperience(0);
            player.setHealth(GameConstants.STARTING_HEALTH);
            playerRepository.update(player);
            notifier.sendNotification(id, PLAYER_HAS_DIED);
        }
    }
}
