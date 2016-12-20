package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.players.PlayerRepository;

import java.time.LocalDateTime;

public class AdjustTodoUrgencyUseCase {
    private final TodoRepository todoRepository;
    private final PlayerRepository playerRepository;

    public AdjustTodoUrgencyUseCase(TodoRepository todoRepository,
                                    PlayerRepository playerRepository) {
        this.todoRepository = todoRepository;
        this.playerRepository = playerRepository;
    }

    public void perform(TodoId id, LocalDateTime currentTime) throws NoSuchTodoException {
        SavedTodo todo = todoRepository.find(id)
                .orElseThrow(() -> new NoSuchTodoException(id));

        if(todoIsPastDue(todo, currentTime) && todo.isIncomplete()) {
            todo.setUrgency(Todo.Urgency.PAST_DUE);
            todoRepository.update(todo);
            playerRepository.find(todo.getPlayerId())
                    .ifPresent(player -> {
                        player.decreaseHealth(GameConstants.EXPIRED_TODO_PENALTY);
                        playerRepository.update(player);
                    });
            System.out.println("nothing");
        }

    }

    private static boolean todoIsPastDue(Todo todo, LocalDateTime currentTime) {
        return todo.getDeadline()
                .map(currentTime::isAfter)
                .orElse(false);
    }
}
