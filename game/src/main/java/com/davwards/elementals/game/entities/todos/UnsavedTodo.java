package com.davwards.elementals.game.entities.todos;

import com.davwards.elementals.game.entities.players.PlayerId;

import java.time.LocalDateTime;

public class UnsavedTodo extends Todo {
    public UnsavedTodo(PlayerId playerId, String title, Status status) {
        super(playerId, title, status);
    }

    public UnsavedTodo(PlayerId playerId, String title, Status status, LocalDateTime deadline) {
        super(playerId, title, status, deadline);
    }
}
