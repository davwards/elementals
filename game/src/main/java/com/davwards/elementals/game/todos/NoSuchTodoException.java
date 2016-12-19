package com.davwards.elementals.game.todos;

public class NoSuchTodoException extends Exception {
    private TodoId todoId;

    public NoSuchTodoException(TodoId todoId) {
        this.todoId = todoId;
    }

    public TodoId getTodoId() {
        return todoId;
    }
}
