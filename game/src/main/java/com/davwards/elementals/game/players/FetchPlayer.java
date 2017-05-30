package com.davwards.elementals.game.players;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

import static com.davwards.elementals.game.support.lookup.GivenRecordExists.givenRecordExists;

public class FetchPlayer {
    public interface Outcome<T> {
        T foundPlayer(SavedPlayer player);
        T noSuchPlayer();
    }

    public <T> T perform(PlayerId id, Outcome<T> handle) {
        return givenRecordExists(
                playerRepository.find(id),
                handle::foundPlayer
        ).otherwise(handle::noSuchPlayer);
    }

    private final PlayerRepository playerRepository;

    public FetchPlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
