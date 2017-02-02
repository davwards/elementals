package com.davwards.elementals.game.entities.tasks;

public class TaskId {
    private final String id;

    public TaskId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskId taskId = (TaskId) o;

        return id.equals(taskId.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return id;
    }
}
