package com.davwards.elementals.game.loot.persistence;

import com.davwards.elementals.game.loot.models.ImmutableSavedLoot;
import com.davwards.elementals.game.loot.models.LootId;
import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.models.UnsavedLoot;
import com.davwards.elementals.game.support.persistence.InMemoryRepositoryOfImmutableRecords;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

public class InMemoryLootRepository
        extends InMemoryRepositoryOfImmutableRecords<UnsavedLoot, SavedLoot, LootId>
        implements LootRepository {

    private final Supplier<LocalDateTime> currentTimeProvider;

    public InMemoryLootRepository() {
        this.currentTimeProvider = LocalDateTime::now;
    }

    public InMemoryLootRepository(Supplier<LocalDateTime> currentTimeProvider) {
        this.currentTimeProvider = currentTimeProvider;
    }

    @Override
    protected LootId createId(String value) {
        return new LootId(UUID.randomUUID().toString());
    }

    @Override
    protected SavedLoot buildSavedRecord(UnsavedLoot record, LootId id) {
        return ImmutableSavedLoot.builder()
                .from(record)
                .id(id)
                .createdAt(currentTimeProvider.get())
                .build();
    }
}
