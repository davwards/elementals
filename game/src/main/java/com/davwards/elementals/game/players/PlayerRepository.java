package com.davwards.elementals.game.players;

import java.util.Optional;

public interface PlayerRepository {
    SavedPlayer save(UnsavedPlayer player);

    Optional<SavedPlayer> find(PlayerId id);

    SavedPlayer save(SavedPlayer player);
}
