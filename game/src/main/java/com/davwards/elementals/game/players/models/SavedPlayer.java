package com.davwards.elementals.game.players.models;

import com.davwards.elementals.game.entities.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedPlayer extends Player, SavedEntity<PlayerId> {
    static ImmutableSavedPlayer copy(SavedPlayer player) {
        return ImmutableSavedPlayer.copyOf(player);
    }
}
