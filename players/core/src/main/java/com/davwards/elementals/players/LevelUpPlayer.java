package com.davwards.elementals.players;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class LevelUpPlayer {
    public interface Outcome<T> {
        T successfullyUpdatedPlayer(SavedPlayer updatedPlayer);

        T playerCannotLevel();

        T noSuchPlayer();
    }

    public <T> T perform(PlayerId playerId, Outcome<T> outcome) {
        return strict(playerRepository.find(playerId))
                .map(player -> checkWhetherPlayerCanLevelUp.perform(player,
                        new CheckWhetherPlayerCanLevelUp.Outcome<T>() {
                            @Override
                            public T playerCanLevelUp(Integer experienceCost) {
                                return outcome.successfullyUpdatedPlayer(
                                        updatePlayer(experienceCost, player)
                                );
                            }

                            @Override
                            public T playerCannotLevelUp(Integer additionalCost) {
                                return outcome.playerCannotLevel();
                            }
                        }
                )).orElseGet(outcome::noSuchPlayer);
    }

    private SavedPlayer updatePlayer(Integer experienceCost, SavedPlayer player) {
        return playerRepository.update(
                SavedPlayer.copy(player)
                        .withExperience(player.experience() - experienceCost)
                        .withLevel(player.level() + 1)
        );
    }

    private final PlayerRepository playerRepository;

    private final CheckWhetherPlayerCanLevelUp checkWhetherPlayerCanLevelUp;

    public LevelUpPlayer(PlayerRepository playerRepository, CheckWhetherPlayerCanLevelUp checkWhetherPlayerCanLevelUp) {
        this.playerRepository = playerRepository;
        this.checkWhetherPlayerCanLevelUp = checkWhetherPlayerCanLevelUp;
    }
}
