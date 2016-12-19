package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.players.PlayerId;

import java.time.LocalDateTime;

public class CreateTodoUseCase {
    private final TodoRepository todoRepository;

    public CreateTodoUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public SavedTodo perform(PlayerId playerId, String title) {
        UnsavedTodo unsavedTodo = new UnsavedTodo(playerId, title, Todo.Status.INCOMPLETE);
        return todoRepository.save(unsavedTodo);
    }

    public SavedTodo perform(PlayerId playerId, String title, LocalDateTime deadline) {
        UnsavedTodo unsavedTodo = new UnsavedTodo(playerId, title, Todo.Status.INCOMPLETE, deadline);
        return todoRepository.save(unsavedTodo);
    }
}
