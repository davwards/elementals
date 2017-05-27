package com.davwards.elementals.game.players;

import com.davwards.elementals.game.entities.CrudRepository;

public interface PlayerRepository extends
        CrudRepository<UnsavedPlayer, SavedPlayer, PlayerId> {
}
