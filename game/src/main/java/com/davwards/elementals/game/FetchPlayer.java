package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.exceptions.NoSuchPlayerException;

import java.util.function.Function;
import java.util.function.Supplier;

public class FetchPlayer {
    private final PlayerRepository playerRepository;

    public FetchPlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public <T> T perform(PlayerId id,
                         Function<SavedPlayer, T> foundPlayer,
                         Supplier<T> noSuchPlayer) {

        return playerRepository
                .find(id)
                .map(foundPlayer)
                .orElseGet(noSuchPlayer);
    }
}
