package com.davwards.elementals.habits.models;

import com.davwards.elementals.players.models.PlayerId;

public interface Habit {
    PlayerId playerId();
    String title();
    Boolean hasUpside();
    Boolean hasDownside();
}
