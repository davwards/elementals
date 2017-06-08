package com.davwards.elementals.game.loot;

import com.davwards.elementals.game.loot.models.ImmutableUnsavedLoot;
import com.davwards.elementals.game.loot.models.KindOfLootId;
import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.persistence.LootRepository;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

import static com.davwards.elementals.game.support.language.StrictOptional.strict;

public class PurchaseLoot {
    public interface Outcome<T> {
        T successfullyPurchasedLoot(SavedLoot savedLoot);
        T noSuchPlayer();
        T notEnoughCoin();
    }

    public <T> T perform(PlayerId playerId, KindOfLootId kindId, Outcome<T> outcome) {
        return strict(playerRepository.find(playerId))
                .map(player -> player.coin() >= costCalculator.cost(kindId)
                        ? outcome.successfullyPurchasedLoot(deductCostAndAddLoot(kindId, player))
                        : outcome.notEnoughCoin())
                .orElseGet(outcome::noSuchPlayer);
    }

    private SavedLoot deductCostAndAddLoot(KindOfLootId kindId, SavedPlayer player) {
        playerRepository.update(SavedPlayer.copy(player)
                .withCoin(player.coin() - costCalculator.cost(kindId))
        );

        return lootRepository.save(
                ImmutableUnsavedLoot.builder()
                        .playerId(player.getId())
                        .kindId(kindId)
                        .build()
        );
    }

    private final LootRepository lootRepository;
    private final PlayerRepository playerRepository;
    private final LootCostCalculator costCalculator;

    public PurchaseLoot(
            LootRepository lootRepository,
            PlayerRepository playerRepository,
            LootCostCalculator costCalculator) {

        this.lootRepository = lootRepository;
        this.playerRepository = playerRepository;
        this.costCalculator = costCalculator;
    }
}
