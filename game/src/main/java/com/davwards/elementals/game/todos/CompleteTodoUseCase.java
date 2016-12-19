package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.players.PlayerRepository;

public class CompleteTodoUseCase {
    private final TodoRepository todoRepository;
    private final PlayerRepository playerRepository;

    public CompleteTodoUseCase(TodoRepository todoRepository, PlayerRepository playerRepository) {
        this.todoRepository = todoRepository;
        this.playerRepository = playerRepository;
    }

    public SavedTodo perform(TodoId id) throws NoSuchTodoException {
        SavedTodo updatedTodo = todoRepository.find(id)
                .map(todo -> {
                    todo.setStatus(Todo.Status.COMPLETE);
                    return todoRepository.save(todo);
                }).orElseThrow(() -> new NoSuchTodoException(id));

        playerRepository.find(updatedTodo.getPlayerId()).ifPresent(player -> {
            player.addExperience(GameConstants.TODO_COMPLETION_PRIZE);
            playerRepository.save(player);
        });

        return updatedTodo;
    }
}
