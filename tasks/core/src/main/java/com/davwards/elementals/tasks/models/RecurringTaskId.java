package com.davwards.elementals.tasks.models;

public class RecurringTaskId {
    private final String id;

    public RecurringTaskId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecurringTaskId that = (RecurringTaskId) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
