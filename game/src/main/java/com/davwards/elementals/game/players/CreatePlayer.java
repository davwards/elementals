package com.davwards.elementals.game.players;

import com.davwards.elementals.game.players.models.ImmutableUnsavedPlayer;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

import static com.davwards.elementals.game.GameConstants.STARTING_COIN;
import static com.davwards.elementals.game.GameConstants.STARTING_HEALTH;

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
                        .health(STARTING_HEALTH)
                        .coin(STARTING_COIN)
                        .build()
                )
        );
    }

    private final PlayerRepository playerRepository;

    public CreatePlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
}
