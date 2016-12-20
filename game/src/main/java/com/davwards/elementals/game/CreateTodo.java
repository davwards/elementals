package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.Todo;
import com.davwards.elementals.game.entities.todos.TodoRepository;
import com.davwards.elementals.game.entities.todos.UnsavedTodo;

import java.time.LocalDateTime;

public class CreateTodo {
    private final TodoRepository todoRepository;

    public CreateTodo(TodoRepository todoRepository) {
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
