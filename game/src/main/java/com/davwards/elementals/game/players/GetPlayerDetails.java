package com.davwards.elementals.game.players;

import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.habits.persistence.HabitRepository;
import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.persistence.LootRepository;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.util.List;

import static com.davwards.elementals.game.support.language.StrictOptional.strict;

public class GetPlayerDetails {

    public interface Outcome<T> {
        T details(SavedPlayer player,
                  List<SavedTask> tasks,
                  List<SavedRecurringTask> recurringTasks,
                  List<SavedHabit> habits,
                  List<SavedLoot> loot);

        T noSuchPlayer();
    }

    public <T> T perform(PlayerId playerId, Outcome<T> outcome) {
        return strict(playerRepository.find(playerId))
                .map(player -> outcome.details(
                        player,
                        taskRepository.findByPlayerId(playerId),
                        recurringTaskRepository.findByPlayerId(playerId),
                        habitRepository.findByPlayerId(playerId),
                        lootRepository.findByPlayerId(playerId)
                ))
                .orElseGet(outcome::noSuchPlayer);
    }

    private final PlayerRepository playerRepository;
    private final TaskRepository taskRepository;
    private final RecurringTaskRepository recurringTaskRepository;
    private final HabitRepository habitRepository;
    private final LootRepository lootRepository;

    public GetPlayerDetails(
            PlayerRepository playerRepository,
            TaskRepository taskRepository,
            RecurringTaskRepository recurringTaskRepository,
            HabitRepository habitRepository,
            LootRepository lootRepository) {

        this.playerRepository = playerRepository;
        this.taskRepository = taskRepository;
        this.recurringTaskRepository = recurringTaskRepository;
        this.habitRepository = habitRepository;
        this.lootRepository = lootRepository;
    }

}
