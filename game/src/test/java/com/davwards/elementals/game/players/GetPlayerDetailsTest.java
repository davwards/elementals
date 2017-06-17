package com.davwards.elementals.game.players;

import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.habits.persistence.HabitRepository;
import com.davwards.elementals.game.habits.persistence.InMemoryHabitRepository;
import com.davwards.elementals.game.loot.models.SavedLoot;
import com.davwards.elementals.game.loot.persistence.InMemoryLootRepository;
import com.davwards.elementals.game.loot.persistence.LootRepository;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.InMemoryRecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.davwards.elementals.game.support.test.Factories.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class GetPlayerDetailsTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private SavedPlayer existingPlayer = playerRepository.save(randomUnsavedPlayer());
    private RecurringTaskRepository recurringTaskRepository = new InMemoryRecurringTaskRepository();
    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private HabitRepository habitRepository = new InMemoryHabitRepository();
    private LootRepository lootRepository = new InMemoryLootRepository();

    private GetPlayerDetails getPlayerDetails = new GetPlayerDetails(
            playerRepository,
            taskRepository,
            recurringTaskRepository,
            habitRepository,
            lootRepository
    );

    @Test
    public void whenPlayerExists_providesPlayerWithTasksHabitsAndLoot() throws Exception {
        String noise = randomString(10);
        PlayerId playerId = existingPlayer.getId();
        PlayerId otherPlayerId = new PlayerId("some other player");

        SavedTask matchingTask = taskRepository.save(randomUnsavedTask().withPlayerId(playerId));
        SavedTask nonmatchingTask = taskRepository.save(randomUnsavedTask().withPlayerId(otherPlayerId));

        SavedRecurringTask matchingRecurringTask = recurringTaskRepository.save(randomUnsavedRecurringTask().withPlayerId(playerId));
        SavedRecurringTask nonmatchingRecurringTask = recurringTaskRepository.save(randomUnsavedRecurringTask().withPlayerId(otherPlayerId));

        SavedHabit matchingHabit = habitRepository.save(randomUnsavedHabit().withPlayerId(playerId));
        SavedHabit nonmatchingHabit = habitRepository.save(randomUnsavedHabit().withPlayerId(otherPlayerId));

        SavedLoot matchingLoot = lootRepository.save(randomUnsavedLoot().withPlayerId(playerId));
        SavedLoot nonmatchingLoot = lootRepository.save(randomUnsavedLoot().withPlayerId(otherPlayerId));

        String result = getPlayerDetails.perform(
                existingPlayer.getId(),
                new GetPlayerDetails.Outcome<String>() {
                    @Override
                    public String details(
                            SavedPlayer player,
                            List<SavedTask> tasks,
                            List<SavedRecurringTask> recurringTasks,
                            List<SavedHabit> habits,
                            List<SavedLoot> loot) {

                        assertThat(player, equalTo(existingPlayer));
                        assertThat(tasks, contains(matchingTask));
                        assertThat(recurringTasks, contains(matchingRecurringTask));
                        assertThat(habits, contains(matchingHabit));
                        assertThat(loot, contains(matchingLoot));
                        return noise;
                    }

                    @Override
                    public String noSuchPlayer() {
                        return null;
                    }
                }
        );

        assertThat(result, equalTo(noise));
    }

    @Test
    public void whenPlayerDoesNotExist_returnsNoSuchPlayerOutcome() throws Exception {
        String noise = randomString(10);

        String result = getPlayerDetails.perform(
                new PlayerId("nonsense"),
                new GetPlayerDetails.Outcome<String>() {
                    @Override
                    public String details(
                            SavedPlayer player,
                            List<SavedTask> tasks,
                            List<SavedRecurringTask> recurringTasks,
                            List<SavedHabit> habits,
                            List<SavedLoot> loot) {
                        return null;
                    }

                    @Override
                    public String noSuchPlayer() {
                        return noise;
                    }
                }
        );

        assertThat(result, equalTo(noise));

    }
}