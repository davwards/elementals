package com.davwards.elementals.game.players;

import com.davwards.elementals.game.players.persistence.PlayerRepository;

public class FetchPlayer {
    public interface Outcome<T> {
        T foundPlayer(SavedPlayer player);
        T noSuchPlayer();
    }

    public <T> T perform(PlayerId id, Outcome<T> handle) {
        return playerRepository
                .find(id)
                .map(handle::foundPlayer)
                .orElseGet(handle::noSuchPlayer);
    }

    private final PlayerRepository playerRepository;

    public FetchPlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
