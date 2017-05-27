package com.davwards.elementals.game.players;

import com.davwards.elementals.game.players.ImmutableUnsavedPlayer;
import com.davwards.elementals.game.players.PlayerRepository;
import com.davwards.elementals.game.players.SavedPlayer;

import java.util.function.Function;

import static com.davwards.elementals.game.GameConstants.STARTING_HEALTH;

public class CreatePlayer {
    private final PlayerRepository playerRepository;

    public CreatePlayer(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public <T> T perform(String playerName,
                         Function<SavedPlayer, T> playerSaved) {

        return playerSaved.apply(
                playerRepository.save(ImmutableUnsavedPlayer.builder()
                        .name(playerName)
                        .experience(0)
                        .health(STARTING_HEALTH)
                        .build()
                )
        );
    }
}
