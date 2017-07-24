package com.davwards.elementals.loot.models;

import com.davwards.elementals.players.models.PlayerId;

public interface Loot {
    PlayerId playerId();
    KindOfLootId kindId();
}
