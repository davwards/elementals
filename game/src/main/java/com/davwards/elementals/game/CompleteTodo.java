package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.Todo;
import com.davwards.elementals.game.entities.todos.TodoId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.todos.TodoRepository;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;

public class CompleteTodo {
    private final TodoRepository todoRepository;
    private final PlayerRepository playerRepository;

    public CompleteTodo(TodoRepository todoRepository, PlayerRepository playerRepository) {
        this.todoRepository = todoRepository;
        this.playerRepository = playerRepository;
    }

    public SavedTodo perform(TodoId id) throws NoSuchTodoException {
        SavedTodo updatedTodo = todoRepository.find(id)
                .map(todo -> {
                    todo.setStatus(Todo.Status.COMPLETE);
                    return todoRepository.update(todo);
                }).orElseThrow(() -> new NoSuchTodoException(id));

        playerRepository.find(updatedTodo.getPlayerId()).ifPresent(player -> {
            player.addExperience(GameConstants.TODO_COMPLETION_PRIZE);
            playerRepository.update(player);
        });

        return updatedTodo;
    }
}
