package com.davwards.elementals.game.loot;

import com.davwards.elementals.game.loot.models.ImmutableUnsavedLoot;
import com.davwards.elementals.game.loot.models.KindOfLootId;
import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.persistence.LootRepository;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class PurchaseLoot {
    public interface Outcome<T> {
        T successfullyPurchasedLoot(SavedLoot savedLoot);
        T noSuchPlayer();
        T notEnoughCoin();
        T notHighEnoughLevel();
    }

    public <T> T perform(PlayerId playerId, KindOfLootId kindId, Outcome<T> outcome) {
        return strict(playerRepository.find(playerId))
                .map(player -> canPurchaseLoot.perform(player, kindId, new CheckWhetherPlayerCanPurchaseLoot.Outcome<T>() {
                    @Override
                    public T playerCanPurchaseLoot(Integer cost) {
                        return outcome.successfullyPurchasedLoot(
                                deductCostAndAddLoot(cost, kindId, player)
                        );
                    }

                    @Override
                    public T playerDoesNotHaveEnoughCoin(Integer coinDeficit) {
                        return outcome.notEnoughCoin();
                    }

                    @Override
                    public T playerIsNotHighEnoughLevel(Integer levelDeficit) {
                        return outcome.notHighEnoughLevel();
                    }
                }))
                .orElseGet(outcome::noSuchPlayer);
    }

    private SavedLoot deductCostAndAddLoot(Integer cost, KindOfLootId kindId, SavedPlayer player) {
        playerRepository.update(SavedPlayer.copy(player)
                .withCoin(player.coin() - cost)
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
    private final CheckWhetherPlayerCanPurchaseLoot canPurchaseLoot;

    public PurchaseLoot(
            LootRepository lootRepository,
            PlayerRepository playerRepository,
            CheckWhetherPlayerCanPurchaseLoot canPurchaseLoot) {

        this.lootRepository = lootRepository;
        this.playerRepository = playerRepository;
        this.canPurchaseLoot = canPurchaseLoot;
    }
}
