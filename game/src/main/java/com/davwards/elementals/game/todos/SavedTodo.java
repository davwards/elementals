package com.davwards.elementals.game.todos;

import java.time.LocalDateTime;
import java.util.Optional;

public class SavedTodo extends Todo {
    private TodoId id;

    public SavedTodo(TodoId id,
                     String title,
                     Urgency urgency,
                     Status status,
                     Optional<LocalDateTime> deadline) {

        super(title, status, urgency, deadline);
        this.id = id;
    }

    public TodoId getId() {
        return id;
    }

    public static SavedTodo clone(SavedTodo savedTodo) {
        return new SavedTodo(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getUrgency(),
                savedTodo.getStatus(),
                savedTodo.getDeadline()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedTodo savedTodo = (SavedTodo) o;

        return id.equals(savedTodo.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
