package com.davwards.elementals.game.loot.models;

import com.davwards.elementals.game.support.persistence.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedLoot extends Loot, SavedEntity<LootId> {
}
