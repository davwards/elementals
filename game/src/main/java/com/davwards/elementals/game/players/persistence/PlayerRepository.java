package com.davwards.elementals.game.players.persistence;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.models.UnsavedPlayer;

public interface PlayerRepository extends
        CrudRepository<UnsavedPlayer, SavedPlayer, PlayerId> {
}
