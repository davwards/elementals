package com.davwards.elementals.game.todos;

import java.util.List;
import java.util.Optional;

public interface TodoRepository {
    List<SavedTodo> allTodos();

    SavedTodo save(UnsavedTodo unsavedTodo);

    Optional<SavedTodo> find(TodoId id);

    SavedTodo save(SavedTodo savedTodo);
}
