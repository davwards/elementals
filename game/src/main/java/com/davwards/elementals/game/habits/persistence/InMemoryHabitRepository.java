package com.davwards.elementals.game.habits.persistence;

import com.davwards.elementals.game.habits.models.HabitId;
import com.davwards.elementals.game.habits.models.ImmutableSavedHabit;
import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.habits.models.UnsavedHabit;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.InMemoryRepositoryOfImmutableRecords;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InMemoryHabitRepository
        extends InMemoryRepositoryOfImmutableRecords<UnsavedHabit, SavedHabit, HabitId>
        implements HabitRepository {

    private final Supplier<LocalDateTime> currentTimeProvider;

    public InMemoryHabitRepository(Supplier<LocalDateTime> currentTimeProvider) {
        this.currentTimeProvider = currentTimeProvider;
    }

    public InMemoryHabitRepository() {
        this.currentTimeProvider = LocalDateTime::now;
    }

    @Override
    protected HabitId createId(String value) {
        return new HabitId(UUID.randomUUID().toString());
    }

    @Override
    protected SavedHabit buildSavedRecord(UnsavedHabit record, HabitId id) {
        return ImmutableSavedHabit.builder()
                .from(record)
                .id(id)
                .createdAt(currentTimeProvider.get())
                .build();
    }

    @Override
    public List<SavedHabit> findByPlayerId(PlayerId playerId) {
        return contents.values().stream()
                .filter(habit -> habit.playerId().equals(playerId))
                .collect(Collectors.toList());
    }
}
