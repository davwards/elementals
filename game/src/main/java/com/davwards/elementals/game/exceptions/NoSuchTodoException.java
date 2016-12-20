package com.davwards.elementals.game.exceptions;

import com.davwards.elementals.game.entities.todos.TodoId;

public class NoSuchTodoException extends RuntimeException {
    private TodoId todoId;

    public NoSuchTodoException(TodoId todoId) {
        this.todoId = todoId;
    }

    public TodoId getTodoId() {
        return todoId;
    }
}
