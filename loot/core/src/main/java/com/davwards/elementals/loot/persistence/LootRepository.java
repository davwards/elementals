package com.davwards.elementals.loot.persistence;

import com.davwards.elementals.loot.models.LootId;
import com.davwards.elementals.loot.models.SavedLoot;
import com.davwards.elementals.loot.models.UnsavedLoot;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepository;

import java.util.List;

public interface LootRepository extends CrudRepository<UnsavedLoot, SavedLoot, LootId> {
    List<SavedLoot> findByPlayerId(PlayerId playerId);
}
