package com.davwards.elementals.game.entities.tasks.recurring;

public class RecurringTaskId {
    private final String value;

    public RecurringTaskId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecurringTaskId that = (RecurringTaskId) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
