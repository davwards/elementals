package com.davwards.elementals.game.players.persistence;

import com.davwards.elementals.game.players.models.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.models.UnsavedPlayer;
import com.davwards.elementals.game.support.persistence.InMemoryRepositoryOfImmutableRecords;

public class InMemoryPlayerRepository
        extends InMemoryRepositoryOfImmutableRecords<UnsavedPlayer, SavedPlayer, PlayerId>
        implements PlayerRepository {

    @Override
    protected PlayerId createId(String value) {
        return new PlayerId(value);
    }

    @Override
    protected SavedPlayer buildSavedRecord(UnsavedPlayer record, PlayerId id) {
        return ImmutableSavedPlayer.builder().from(record).id(id).build();
    }
}
