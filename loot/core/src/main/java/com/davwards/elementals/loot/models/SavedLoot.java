package com.davwards.elementals.loot.models;

import com.davwards.elementals.support.persistence.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedLoot extends Loot, SavedEntity<LootId> {
}
