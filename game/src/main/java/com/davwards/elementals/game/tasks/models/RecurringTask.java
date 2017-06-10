package com.davwards.elementals.game.tasks.models;

import com.davwards.elementals.game.players.models.PlayerId;

import java.time.Period;

public interface RecurringTask {
    PlayerId playerId();
    String title();
    String cadence();
    Period duration();
}
