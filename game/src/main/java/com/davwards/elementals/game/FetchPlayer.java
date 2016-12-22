package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.exceptions.NoSuchPlayerException;

public class FetchPlayer {
    private final PlayerRepository playerRepository;

    public FetchPlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public SavedPlayer perform(PlayerId id) {
        return playerRepository.find(id).orElseThrow(() -> new NoSuchPlayerException(id));
    }
}
