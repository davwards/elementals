package com.davwards.elementals.game.inmemorypersistence;

import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.TodoId;
import com.davwards.elementals.game.entities.todos.TodoRepository;
import com.davwards.elementals.game.entities.todos.UnsavedTodo;

import java.util.*;

public class InMemoryTodoRepository implements TodoRepository {
    private Map<TodoId, SavedTodo> contents = new HashMap<>();

    @Override
    public List<SavedTodo> all() {
        return new ArrayList<>(contents.values());
    }

    @Override
    public SavedTodo save(UnsavedTodo unsavedTodo) {
        TodoId id = new TodoId(UUID.randomUUID().toString());

        SavedTodo savedTodo = new SavedTodo(
                id,
                unsavedTodo.getPlayerId(),
                unsavedTodo.getTitle(),
                unsavedTodo.getUrgency(),
                unsavedTodo.getStatus(),
                unsavedTodo.getDeadline()
        );

        contents.put(id, savedTodo);
        return SavedTodo.clone(savedTodo);
    }

    @Override
    public Optional<SavedTodo> find(TodoId id) {
        return Optional.ofNullable(contents.get(id))
                .map(SavedTodo::clone);
    }

    @Override
    public SavedTodo update(SavedTodo savedTodo) {
        contents.put(savedTodo.getId(), SavedTodo.clone(savedTodo));
        return savedTodo;
    }
}
