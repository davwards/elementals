package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.Todo;
import com.davwards.elementals.game.entities.todos.TodoId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.todos.TodoRepository;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;

import java.time.LocalDateTime;

public class UpdateTodoStatus {
    private final TodoRepository todoRepository;
    private final PlayerRepository playerRepository;

    public UpdateTodoStatus(TodoRepository todoRepository,
                            PlayerRepository playerRepository) {
        this.todoRepository = todoRepository;
        this.playerRepository = playerRepository;
    }

    public void perform(TodoId id, LocalDateTime currentTime) throws NoSuchTodoException {
        SavedTodo todo = todoRepository.find(id)
                .orElseThrow(() -> new NoSuchTodoException(id));

        if(todoIsPastDue(todo, currentTime) && todo.isIncomplete()) {
            todo.setStatus(Todo.Status.PAST_DUE);
            todoRepository.update(todo);
            playerRepository.find(todo.getPlayerId())
                    .ifPresent(player -> {
                        player.decreaseHealth(GameConstants.EXPIRED_TODO_PENALTY);
                        playerRepository.update(player);
                    });
        }
    }

    private static boolean todoIsPastDue(Todo todo, LocalDateTime currentTime) {
        return todo.getDeadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }
}