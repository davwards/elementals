package com.davwards.elementals.scheduler;

import com.davwards.elementals.game.ResurrectPlayer;
import com.davwards.elementals.game.UpdateTodoStatus;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.todos.TodoRepository;
import org.springframework.scheduling.annotation.Scheduled;

public class Scheduler {
    private final TimeProvider timeProvider;
    private final UpdateTodoStatus updateTodoStatus;
    private final ResurrectPlayer resurrectPlayer;
    private final PlayerRepository playerRepository;
    private final TodoRepository todoRepository;

    public Scheduler(TimeProvider timeProvider,
                     UpdateTodoStatus updateTodoStatus,
                     ResurrectPlayer resurrectPlayer,
                     PlayerRepository playerRepository,
                     TodoRepository todoRepository) {
        this.timeProvider = timeProvider;
        this.updateTodoStatus = updateTodoStatus;
        this.resurrectPlayer = resurrectPlayer;
        this.playerRepository = playerRepository;
        this.todoRepository = todoRepository;
    }

    @Scheduled(fixedDelay = 1000)
    public void updateTodos() {
        todoRepository.all().forEach(todo -> updateTodoStatus.perform(todo.getId(), timeProvider.currentTime()));
    }

    @Scheduled(fixedDelay = 1000)
    public void resurrectDeadPlayers() {
        playerRepository.all().forEach(player -> resurrectPlayer.perform(player.getId()));
    }
}
