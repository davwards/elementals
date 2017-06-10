package com.davwards.elementals.game.leveling;

import com.davwards.elementals.game.players.models.Player;

public interface CheckWhetherPlayerCanLevelUp {
    interface Outcome<T> {
        T playerCanLevelUp(Integer experienceCost);

        T playerCannotLevelUp(Integer additionalCost);
    }

    <T> T perform(Player player, Outcome<T> outcome);
}
