package com.davwards.elementals.players;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class FetchPlayer {
    public interface Outcome<T> {
        T foundPlayer(SavedPlayer player);
        T noSuchPlayer();
    }

    public <T> T perform(PlayerId id, Outcome<T> outcome) {
        return strict(playerRepository.find(id))
                .map(outcome::foundPlayer)
                .orElseGet(outcome::noSuchPlayer);
    }

    private final PlayerRepository playerRepository;

    public FetchPlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
