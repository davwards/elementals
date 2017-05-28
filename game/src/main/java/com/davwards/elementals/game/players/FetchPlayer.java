package com.davwards.elementals.game.players;

public class FetchPlayer {
    private final PlayerRepository playerRepository;

    public FetchPlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public interface Outcome<T> {
        T foundPlayer(SavedPlayer player);
        T noSuchPlayer();
    }

    public <T> T perform(PlayerId id,
                         Outcome<T> handle) {

        return playerRepository
                .find(id)
                .map(handle::foundPlayer)
                .orElseGet(handle::noSuchPlayer);
    }
}
