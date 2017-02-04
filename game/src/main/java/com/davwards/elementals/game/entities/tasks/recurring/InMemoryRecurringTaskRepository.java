package com.davwards.elementals.game.entities.tasks.recurring;

import java.util.*;

public class InMemoryRecurringTaskRepository implements RecurringTaskRepository {
    private Map<RecurringTaskId, SavedRecurringTask> records = new HashMap<>();

    @Override
    public SavedRecurringTask save(UnsavedRecurringTask record) {
        RecurringTaskId id = new RecurringTaskId(UUID.randomUUID().toString());
        SavedRecurringTask savedRecurringTask = new SavedRecurringTask(id, record.getTitle());
        records.put(id, savedRecurringTask);
        return new SavedRecurringTask(savedRecurringTask.getId(), savedRecurringTask.getTitle());
    }

    @Override
    public SavedRecurringTask update(SavedRecurringTask record) {
        SavedRecurringTask newRecord = new SavedRecurringTask(record.getId(), record.getTitle());
        records.put(record.getId(), newRecord);
        return newRecord;
    }

    @Override
    public Optional<SavedRecurringTask> find(RecurringTaskId id) {
        return Optional.ofNullable(records.get(id))
                .map(record -> new SavedRecurringTask(
                        record.getId(), record.getTitle()
                ));
    }

    @Override
    public List<SavedRecurringTask> all() {
        return new ArrayList<>(records.values());
    }
}
