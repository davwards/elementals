package com.davwards.elementals.api.habits;

import com.davwards.elementals.habits.models.SavedHabit;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HabitResponse {
    @JsonProperty private final String id;
    @JsonProperty private final String playerId;
    @JsonProperty private final String title;
    @JsonProperty private final Boolean upside;
    @JsonProperty private final Boolean downside;
    @JsonProperty private final String createdAt;

    public HabitResponse(SavedHabit habit) {
        this.id = habit.getId().toString();
        this.playerId = habit.playerId().toString();
        this.title = habit.title();
        this.upside = habit.hasUpside();
        this.downside = habit.hasDownside();
        this.createdAt = habit.createdAt().toString();
    }
}
