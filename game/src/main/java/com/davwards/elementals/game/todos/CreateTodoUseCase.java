package com.davwards.elementals.game.todos;

import java.time.LocalDateTime;

public class CreateTodoUseCase {
    private final TodoRepository todoRepository;

    public CreateTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public SavedTodo perform(String title) {
        UnsavedTodo unsavedTodo = new UnsavedTodo(title, Todo.Status.INCOMPLETE);
        return todoRepository.save(unsavedTodo);
    }

    public SavedTodo perform(String title, LocalDateTime deadline) {
        UnsavedTodo unsavedTodo = new UnsavedTodo(title, Todo.Status.INCOMPLETE, deadline);
        return todoRepository.save(unsavedTodo);
    }
}
