package com.davwards.elementals.game.loot.persistence;

import com.davwards.elementals.game.loot.models.LootId;
import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.models.UnsavedLoot;
import com.davwards.elementals.game.support.persistence.CrudRepository;

public interface LootRepository extends CrudRepository<UnsavedLoot, SavedLoot, LootId> {
}
