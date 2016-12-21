package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.exceptions.NoSuchPlayerException;

public class ResurrectPlayer {
    private final PlayerRepository playerRepository;

    public ResurrectPlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void perform(PlayerId id) {
        SavedPlayer player = playerRepository.find(id)
                .orElseThrow(() -> new NoSuchPlayerException(id));

        if(!player.isAlive()) {
            player.setExperience(0);
            player.setHealth(GameConstants.STARTING_HEALTH);
            playerRepository.update(player);
        }
    }
}
