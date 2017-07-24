package com.davwards.elementals.players.persistence;

import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.models.UnsavedPlayer;
import com.davwards.elementals.support.persistence.CrudRepository;

public interface PlayerRepository extends
        CrudRepository<UnsavedPlayer, SavedPlayer, PlayerId> {
}
