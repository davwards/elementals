package com.davwards.elementals.game.entities.players;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<UnsavedPlayer, SavedPlayer, PlayerId> {
    SavedPlayer save(UnsavedPlayer player);

    Optional<SavedPlayer> find(PlayerId id);

    SavedPlayer update(SavedPlayer player);
}
