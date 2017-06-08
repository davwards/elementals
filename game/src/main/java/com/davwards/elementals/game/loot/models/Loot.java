package com.davwards.elementals.game.loot.models;

import com.davwards.elementals.game.players.models.PlayerId;

public interface Loot {
    PlayerId playerId();
    KindOfLootId kindId();
}
