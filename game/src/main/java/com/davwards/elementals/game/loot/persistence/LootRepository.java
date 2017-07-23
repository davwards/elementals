package com.davwards.elementals.game.loot.persistence;

import com.davwards.elementals.game.loot.models.LootId;
import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.models.UnsavedLoot;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepository;

import java.util.List;

public interface LootRepository extends CrudRepository<UnsavedLoot, SavedLoot, LootId> {
    List<SavedLoot> findByPlayerId(PlayerId playerId);
}
