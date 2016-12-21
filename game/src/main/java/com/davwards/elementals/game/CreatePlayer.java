package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;

public class CreatePlayer {
    private final PlayerRepository playerRepository;

    public CreatePlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public SavedPlayer perform(String playerName) {
        return playerRepository.save(new UnsavedPlayer(playerName));
    }
}
