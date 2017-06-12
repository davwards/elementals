package com.davwards.elementals.game.habits.models;

public class HabitId {
    private final String value;

    public HabitId(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HabitId habitId = (HabitId) o;

        return value.equals(habitId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
