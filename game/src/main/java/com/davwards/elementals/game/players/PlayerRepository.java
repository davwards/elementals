package com.davwards.elementals.game.players;

import com.davwards.elementals.game.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<UnsavedPlayer, SavedPlayer, PlayerId> {
    SavedPlayer save(UnsavedPlayer player);

    Optional<SavedPlayer> find(PlayerId id);

    SavedPlayer update(SavedPlayer player);
}
