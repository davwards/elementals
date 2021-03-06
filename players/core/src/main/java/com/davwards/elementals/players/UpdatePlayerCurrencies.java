package com.davwards.elementals.players;

import com.davwards.elementals.players.models.ImmutableSavedPlayer;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class UpdatePlayerCurrencies {
    public interface Outcome<T> {
        T updatedPlayer(SavedPlayer player);
        T noSuchPlayer();
    }

    public <T> T perform(PlayerId playerId, CurrencyChanges changes, Outcome<T> outcome) {
        return strict(playerRepository.find(playerId))
                .map(player -> outcome.updatedPlayer(
                        playerRepository.update(
                                changes.updatePlayer(player))))
                .orElseGet(outcome::noSuchPlayer);
    }

    private final PlayerRepository playerRepository;

    public UpdatePlayerCurrencies(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public static class CurrencyChanges {
        private Integer health = 0;
        private Integer experience = 0;
        private Integer coin = 0;
        private Integer level = 0;

        public CurrencyChanges health(Integer value) {
            this.health = value;
            return this;
        }

        public CurrencyChanges experience(Integer value) {
            this.experience = value;
            return this;
        }

        public CurrencyChanges coin(Integer value) {
            this.coin = value;
            return this;
        }

        public CurrencyChanges level(Integer value) {
            this.level = value;
            return this;
        }

        SavedPlayer updatePlayer(SavedPlayer player) {
            return ImmutableSavedPlayer.copyOf(player)
                    .withExperience(player.experience() + this.experience)
                    .withHealth(player.health() + this.health)
                    .withCoin(player.coin() + this.coin)
                    .withLevel(player.level() + this.level);
        }
    }
}
