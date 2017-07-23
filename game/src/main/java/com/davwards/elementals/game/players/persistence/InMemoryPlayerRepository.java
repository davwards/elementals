package com.davwards.elementals.game.players.persistence;

import com.davwards.elementals.game.players.models.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.models.UnsavedPlayer;
import com.davwards.elementals.support.persistence.InMemoryRepositoryOfImmutableRecords;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class InMemoryPlayerRepository
        extends InMemoryRepositoryOfImmutableRecords<UnsavedPlayer, SavedPlayer, PlayerId>
        implements PlayerRepository {

    private final Supplier<LocalDateTime> currentTimeProvider;

    public InMemoryPlayerRepository() {
        currentTimeProvider = LocalDateTime::now;
    }

    public InMemoryPlayerRepository(Supplier<LocalDateTime> currentTimeProvider) {
        this.currentTimeProvider = currentTimeProvider;
    }

    @Override
    protected PlayerId createId(String value) {
        return new PlayerId(value);
    }

    @Override
    protected SavedPlayer buildSavedRecord(UnsavedPlayer record, PlayerId id) {
        return ImmutableSavedPlayer.builder()
                .from(record)
                .id(id)
                .createdAt(currentTimeProvider.get())
                .build();
    }
}
