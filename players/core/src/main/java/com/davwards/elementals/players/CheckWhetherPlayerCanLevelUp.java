package com.davwards.elementals.players;

import com.davwards.elementals.players.models.Player;

public interface CheckWhetherPlayerCanLevelUp {
    interface Outcome<T> {
        T playerCanLevelUp(Integer experienceCost);

        T playerCannotLevelUp(Integer additionalCost);
    }

    <T> T perform(Player player, Outcome<T> outcome);

    class InProcess implements CheckWhetherPlayerCanLevelUp {
        public <T> T perform(Player player, Outcome<T> outcome) {
            Integer costOfNextLevel = costOfLevel(player.level() + 1);

            return player.experience() >= costOfNextLevel
                    ? outcome.playerCanLevelUp(costOfNextLevel)
                    : outcome.playerCannotLevelUp(costOfNextLevel - player.experience());
        }

        private Integer costOfLevel(Integer level) {
            return 50 + (level - 2) * 20;
        }
    }
}
