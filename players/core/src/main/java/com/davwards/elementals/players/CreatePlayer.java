package com.davwards.elementals.players;

import com.davwards.elementals.players.models.ImmutableUnsavedPlayer;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;

public class CreatePlayer {
    public interface Outcome<T> {
        T playerSaved(SavedPlayer player);
    }

    public <T> T perform(String playerName, Outcome<T> outcome) {
        return outcome.playerSaved(
                playerRepository.save(ImmutableUnsavedPlayer.builder()
                        .name(playerName)
                        .experience(0)
                        .level(1)
                        .health(PlayerGameConstants.STARTING_HEALTH)
                        .coin(PlayerGameConstants.STARTING_COIN)
                        .build()
                )
        );
    }

    private final PlayerRepository playerRepository;

    public CreatePlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
