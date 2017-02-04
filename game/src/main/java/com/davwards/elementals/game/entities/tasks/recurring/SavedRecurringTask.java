package com.davwards.elementals.game.entities.tasks.recurring;

import com.davwards.elementals.game.entities.SavedEntity;

public class SavedRecurringTask extends RecurringTask implements SavedEntity<RecurringTaskId> {
    private final RecurringTaskId id;

    public SavedRecurringTask(RecurringTaskId id, String title) {
        super(title);
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedRecurringTask that = (SavedRecurringTask) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public RecurringTaskId getId() {
        return this.id;
    }
}
