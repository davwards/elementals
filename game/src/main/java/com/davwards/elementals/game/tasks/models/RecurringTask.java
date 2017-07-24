package com.davwards.elementals.game.tasks.models;

import com.davwards.elementals.players.models.PlayerId;

import java.time.Period;

public interface RecurringTask {
    PlayerId playerId();
    String title();
    String cadence();
    Period duration();
}
