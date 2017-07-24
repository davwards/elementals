package com.davwards.elementals.loot;

import com.davwards.elementals.loot.models.KindOfLootId;
import com.davwards.elementals.players.models.Player;
import com.davwards.elementals.support.language.StrictOptional;

import java.util.Map;

import static com.davwards.elementals.loot.models.KindOfLootId.COPPER_SWORD;
import static com.davwards.elementals.loot.models.KindOfLootId.VORPAL_SWORD;
import static com.davwards.elementals.support.language.BuildableMap.mappingOf;

public interface CheckWhetherPlayerCanPurchaseLoot {
    interface Outcome<T> {
        T playerCanPurchaseLoot(Integer cost);
        T playerDoesNotHaveEnoughCoin(Integer coinDeficit);
        T playerIsNotHighEnoughLevel(Integer levelDeficit);
    }

    <T> T perform(Player player, KindOfLootId kindOfLootId, Outcome<T> outcome);

    class InProcess implements CheckWhetherPlayerCanPurchaseLoot {

        private static Map<KindOfLootId, LootRequirements> requirements =
                mappingOf(KindOfLootId.class, LootRequirements.class)
                        .with(COPPER_SWORD, new LootRequirements(20, 1))
                        .with(VORPAL_SWORD, new LootRequirements(1000, 80));

        @Override
        public <T> T perform(Player player, KindOfLootId kindOfLootId, Outcome<T> outcome) {
            return StrictOptional.of(requirements.get(kindOfLootId))
                    .map(reqs -> {
                        if(player.level() < reqs.level)
                            return outcome.playerIsNotHighEnoughLevel(reqs.level - player.level());
                        if(player.coin() < reqs.coin)
                            return outcome.playerDoesNotHaveEnoughCoin(reqs.coin - player.coin());

                        return outcome.playerCanPurchaseLoot(reqs.coin);
                    })
                    .orElseThrow(() -> new RuntimeException("Missing requirements for kind of loot: " + kindOfLootId));
        }

        private static class LootRequirements {
            public final Integer coin;
            public final Integer level;

            private LootRequirements(Integer coin, Integer level) {
                this.coin = coin;
                this.level = level;
            }
        }
    }
}
