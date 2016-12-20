package com.davwards.elementals.game.entities.todos;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.TodoId;
import com.davwards.elementals.game.entities.todos.UnsavedTodo;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends CrudRepository<UnsavedTodo, SavedTodo, TodoId> {
    List<SavedTodo> all();

    SavedTodo save(UnsavedTodo unsavedTodo);

    Optional<SavedTodo> find(TodoId id);

    SavedTodo update(SavedTodo savedTodo);
}
