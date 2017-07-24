package com.davwards.elementals.loot.persistence;

import com.davwards.elementals.loot.models.ImmutableSavedLoot;
import com.davwards.elementals.loot.models.LootId;
import com.davwards.elementals.loot.models.SavedLoot;
import com.davwards.elementals.loot.models.UnsavedLoot;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.InMemoryRepositoryOfImmutableRecords;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    public List<SavedLoot> findByPlayerId(PlayerId playerId) {
        return contents.values().stream()
                .filter(loot -> loot.playerId().equals(playerId))
                .collect(Collectors.toList());
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
