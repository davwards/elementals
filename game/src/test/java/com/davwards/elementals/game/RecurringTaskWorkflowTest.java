package com.davwards.elementals.game;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.CreateRecurringTask;
import com.davwards.elementals.game.tasks.SpawnRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.InMemoryRecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class RecurringTaskWorkflowTest {
    private InMemoryRecurringTaskRepository recurringTaskRepository = new InMemoryRecurringTaskRepository();
    private InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();

    private LocalDateTime todayAtNoon = LocalDateTime.of(2017, 11, 5, 12, 0);
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

    private void thenThePlayerHasATaskWithTheCorrectDetails(Optional<SavedTask> spawnedTask, SavedRecurringTask recurringTask) {
        assertThat(spawnedTask.isPresent(), is(true));
        assertThat(spawnedTask.get().title(), equalTo(recurringTask.title()));
        assertThat(spawnedTask.get().playerId(), equalTo(recurringTask.playerId()));
        assertThat(spawnedTask.get().deadline().get(), equalTo(tonightAtMidnight.plusDays(1)));
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
        return createRecurringTask.perform(
                "Daily Task",
                new PlayerId("the-player-id"),
                "DAILY",
                Period.ofDays(1),
                task -> task
        );
    }

}
