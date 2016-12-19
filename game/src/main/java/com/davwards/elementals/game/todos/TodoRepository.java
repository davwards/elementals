package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends CrudRepository<UnsavedTodo, SavedTodo, TodoId> {
    List<SavedTodo> all();

    SavedTodo save(UnsavedTodo unsavedTodo);

    Optional<SavedTodo> find(TodoId id);

    SavedTodo update(SavedTodo savedTodo);
}
