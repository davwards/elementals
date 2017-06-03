package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.InMemoryRepositoryOfImmutableRecords;
import com.davwards.elementals.game.tasks.models.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InMemoryTaskRepository
        extends InMemoryRepositoryOfImmutableRecords<UnsavedTask, SavedTask, TaskId>
        implements TaskRepository {

    private final Supplier<LocalDateTime> currentTimeProvider;

    public InMemoryTaskRepository() {
        this.currentTimeProvider = LocalDateTime::now;
    }

    public InMemoryTaskRepository(Supplier<LocalDateTime> currentTimeProvider) {
        this.currentTimeProvider = currentTimeProvider;
    }

    @Override
    protected TaskId createId(String value) {
        return new TaskId(value);
    }

    @Override
    protected SavedTask buildSavedRecord(UnsavedTask record, TaskId id) {
        return ImmutableSavedTask.builder()
                .from(record)
                .id(id)
                .createdAt(currentTimeProvider.get())
                .build();
    }

    @Override
    public List<SavedTask> findByPlayerId(PlayerId playerId) {
        return contents.values().stream()
                .filter(task -> task.playerId().equals(playerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SavedTask> findByParentRecurringTaskId(RecurringTaskId recurringTaskId) {
        return contents.values().stream()
                .filter(task -> task.parentRecurringTaskId().map(recurringTaskId::equals).orElse(false))
                .collect(Collectors.toList());
    }
}
