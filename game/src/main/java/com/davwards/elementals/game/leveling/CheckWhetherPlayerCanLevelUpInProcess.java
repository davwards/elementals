package com.davwards.elementals.game.leveling;

import com.davwards.elementals.game.players.models.Player;

public class CheckWhetherPlayerCanLevelUpInProcess implements CheckWhetherPlayerCanLevelUp {
    public <T> T perform(Player player, CheckWhetherPlayerCanLevelUp.Outcome<T> outcome) {
        Integer costOfNextLevel = costOfLevel(player.level() + 1);

        return player.experience() >= costOfNextLevel
                ? outcome.playerCanLevelUp(costOfNextLevel)
                : outcome.playerCannotLevelUp(costOfNextLevel - player.experience());
    }

    private Integer costOfLevel(Integer level) {
        return 50 + (level - 2) * 20;
    }
}
