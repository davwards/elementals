package com.davwards.elementals.game.entities.players;

import com.davwards.elementals.game.entities.CrudRepository;

public interface PlayerRepository extends
        CrudRepository<UnsavedPlayer, SavedPlayer, PlayerId> {
}
