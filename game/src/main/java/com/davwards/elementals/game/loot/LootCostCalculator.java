package com.davwards.elementals.game.loot;

import com.davwards.elementals.game.loot.models.KindOfLootId;

public interface LootCostCalculator {
    Integer cost(KindOfLootId kind);
}
