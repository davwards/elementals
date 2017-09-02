package com.davwards.elementals.players;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;

import java.util.function.Function;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class LevelUpPlayer {
    public interface Outcome<T> {
        T successfullyUpdatedPlayer(SavedPlayer updatedPlayer);
        T playerCannotLevel();
        T noSuchPlayer();
    }

    public <T> T perform(PlayerId playerId, Outcome<T> outcome) {
        return strict(playerRepository.find(playerId))
                .map(levelUpPlayerIfPossible(outcome))
                .orElseGet(outcome::noSuchPlayer);
    }

    private <T> Function<SavedPlayer, T> levelUpPlayerIfPossible(final Outcome<T> outcome) {
        return player -> checkWhetherPlayerCanLevelUp.perform(
                player,
                new CheckWhetherPlayerCanLevelUp.Outcome<T>() {
                    @Override
                    public T playerCanLevelUp(Integer experienceCost) {
                        return updatePlayerCurrencies.perform(
                                player.getId(),
                                new UpdatePlayerCurrencies.CurrencyChanges()
                                        .experience(-experienceCost)
                                        .level(1),
                                new UpdatePlayerCurrencies.Outcome<T>() {
                                    @Override
                                    public T updatedPlayer(SavedPlayer player) {
                                        return outcome.successfullyUpdatedPlayer(player);
                                    }

                                    @Override
                                    public T noSuchPlayer() {
                                        return outcome.noSuchPlayer();
                                    }
                                }
                        );
                    }

                    @Override
                    public T playerCannotLevelUp(Integer additionalCost) {
                        return outcome.playerCannotLevel();
                    }
                }
        );
    }

    private final PlayerRepository playerRepository;
    private final CheckWhetherPlayerCanLevelUp checkWhetherPlayerCanLevelUp;
    private final UpdatePlayerCurrencies updatePlayerCurrencies;

    public LevelUpPlayer(PlayerRepository playerRepository, CheckWhetherPlayerCanLevelUp checkWhetherPlayerCanLevelUp, UpdatePlayerCurrencies updatePlayerCurrencies) {
        this.playerRepository = playerRepository;
        this.checkWhetherPlayerCanLevelUp = checkWhetherPlayerCanLevelUp;
        this.updatePlayerCurrencies = updatePlayerCurrencies;
    }
}
