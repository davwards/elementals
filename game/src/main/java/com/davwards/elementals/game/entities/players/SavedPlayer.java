package com.davwards.elementals.game.entities.players;

import com.davwards.elementals.game.entities.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedPlayer extends Player, SavedEntity<PlayerId> {
}
