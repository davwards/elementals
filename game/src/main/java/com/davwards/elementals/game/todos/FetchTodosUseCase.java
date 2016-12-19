package com.davwards.elementals.game.todos;

import java.util.List;

public class FetchTodosUseCase {
    private final TodoRepository todoRepository;

    public FetchTodosUseCase(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<SavedTodo> perform() {
        return todoRepository.all();
    }
}
