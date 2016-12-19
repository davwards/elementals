package com.davwards.elementals.game.todos;

import java.time.LocalDateTime;

public class UnsavedTodo extends Todo {
    public UnsavedTodo(String title, Status status) {
        super(title, status);
    }

    public UnsavedTodo(String title, Status status, LocalDateTime deadline) {
        super(title, status, deadline);
    }
}
