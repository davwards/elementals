package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;

import java.util.function.Function;

public class CreatePlayer {
    private final PlayerRepository playerRepository;

    public CreatePlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public <T> T perform(String playerName,
                         Function<SavedPlayer, T> playerSaved) {

        return playerSaved.apply(
                playerRepository.save(new UnsavedPlayer(playerName))
        );
    }
}
