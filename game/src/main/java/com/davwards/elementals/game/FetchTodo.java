package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.TodoId;
import com.davwards.elementals.game.entities.todos.TodoRepository;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;

public class FetchTodo {
    private final TodoRepository todoRepository;

    public FetchTodo(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public SavedTodo perform(TodoId id) {
        return todoRepository.find(id).orElseThrow(() -> new NoSuchTodoException(id));
    }
}
