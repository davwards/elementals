package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.TodoRepository;

import java.util.List;

public class FetchTodos {
    private final TodoRepository todoRepository;

    public FetchTodos(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<SavedTodo> perform() {
        return todoRepository.all();
    }
}
