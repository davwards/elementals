package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.support.persistence.InMemoryRepositoryOfImmutableRecords;
import com.davwards.elementals.game.tasks.models.ImmutableSavedRecurringTask;
import com.davwards.elementals.game.tasks.models.RecurringTaskId;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.UnsavedRecurringTask;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class InMemoryRecurringTaskRepository
        extends InMemoryRepositoryOfImmutableRecords<UnsavedRecurringTask, SavedRecurringTask, RecurringTaskId>
        implements RecurringTaskRepository {

    private final Supplier<LocalDateTime> currentTimeProvider;

    public InMemoryRecurringTaskRepository() {
        this.currentTimeProvider = LocalDateTime::now;
    }

    public InMemoryRecurringTaskRepository(Supplier<LocalDateTime> currentTimeProvider) {
        this.currentTimeProvider = currentTimeProvider;
    }

    @Override
    protected RecurringTaskId createId(String value) {
        return new RecurringTaskId(value);
    }

    @Override
    protected SavedRecurringTask buildSavedRecord(UnsavedRecurringTask record, RecurringTaskId id) {
        return ImmutableSavedRecurringTask.builder()
                .from(record)
                .id(id)
                .createdAt(currentTimeProvider.get())
                .build();
    }
}
