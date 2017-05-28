package com.davwards.elementals.game.players.persistence;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.players.PlayerId;
import com.davwards.elementals.game.players.SavedPlayer;
import com.davwards.elementals.game.players.UnsavedPlayer;

public interface PlayerRepository extends
        CrudRepository<UnsavedPlayer, SavedPlayer, PlayerId> {
}
