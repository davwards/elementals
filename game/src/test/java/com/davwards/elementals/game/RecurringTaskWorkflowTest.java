package com.davwards.elementals.game;

import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.InMemoryRecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.recurring.CreateRecurringTask;
import com.davwards.elementals.game.tasks.recurring.SpawnRecurringTask;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.stream.Stream;

import static com.davwards.elementals.support.test.Factories.randomUnsavedPlayer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;

public class RecurringTaskWorkflowTest {
    private final InMemoryRecurringTaskRepository recurringTaskRepository =
            new InMemoryRecurringTaskRepository();
    private final InMemoryTaskRepository taskRepository =
            new InMemoryTaskRepository();
    private final InMemoryPlayerRepository playerRepository =
            new InMemoryPlayerRepository();

    private CreateRecurringTask createRecurringTask = new CreateRecurringTask(recurringTaskRepository);
    private LocalDateTime tonightAtMidnight = LocalDateTime.of(2017, 11, 6, 0, 0);

    private SpawnRecurringTask spawnRecurringTask = new SpawnRecurringTask(
            recurringTaskRepository,
            taskRepository,
            (lastOccurrence, cadence) -> Stream.of(tonightAtMidnight).iterator()
    );

    @Test
    public void recurringTaskWorkflow() throws Exception {
        SavedRecurringTask recurringTask =
                givenARecurringTaskThatIsDueToSpawn();

        Optional<SavedTask> spawnedTask =
                whenTheSpawnUseCaseRuns(recurringTask);

        thenThePlayerHasATaskWithTheCorrectDetails(spawnedTask, recurringTask);
    }

    private void thenThePlayerHasATaskWithTheCorrectDetails(Optional<SavedTask> maybeSpawnedTask, SavedRecurringTask recurringTask) {
        SavedTask spawnedTask = maybeSpawnedTask.get();

        assertThat(spawnedTask.title(), equalTo(recurringTask.title()));
        assertThat(spawnedTask.playerId(), equalTo(recurringTask.playerId()));
        assertThat(spawnedTask.deadline().get(), equalTo(tonightAtMidnight.plusDays(1)));

        assertThat(taskRepository.findByPlayerId(recurringTask.playerId()), hasItem(spawnedTask));
    }

    private Optional<SavedTask> whenTheSpawnUseCaseRuns(SavedRecurringTask task) {
        return spawnRecurringTask.perform(
                task.getId(),
                tonightAtMidnight,
                new SpawnRecurringTask.Outcome<Optional<SavedTask>>() {
                    @Override
                    public Optional<SavedTask> spawnedNewTask(SavedTask task) {
                        return Optional.of(task);
                    }

                    @Override
                    public Optional<SavedTask> taskNotDueToSpawn() {
                        return Optional.empty();
                    }

                    @Override
                    public Optional<SavedTask> noSuchRecurringTask() {
                        return Optional.empty();
                    }
                }
        );
    }

    private SavedRecurringTask givenARecurringTaskThatIsDueToSpawn() {
        SavedPlayer player = playerRepository.save(randomUnsavedPlayer());
        return createRecurringTask.perform(
                "Daily Task",
                player.getId(),
                "DAILY",
                Period.ofDays(1),
                task -> task
        );
    }

}
