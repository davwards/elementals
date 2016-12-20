package com.davwards.elementals.game.entities.todos;

public class TodoId {
    private final String id;

    public TodoId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TodoId todoId = (TodoId) o;

        return id.equals(todoId.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String toString() {
        return id;
    }
}
