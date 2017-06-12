package com.davwards.elementals.game.habits.models;

import com.davwards.elementals.game.players.models.PlayerId;

public interface Habit {
    PlayerId playerId();
    String title();
    Boolean hasUpside();
    Boolean hasDownside();
}
