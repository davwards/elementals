package com.davwards.elementals.game;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.CreateRecurringTask;
import com.davwards.elementals.game.tasks.GetPlayerTasks;
import com.davwards.elementals.game.tasks.SpawnRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.InMemoryRecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.davwards.elementals.game.support.test.Factories.randomString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;

public class RecurringTaskWorkflowTest {
    private InMemoryRecurringTaskRepository recurringTaskRepository = new InMemoryRecurringTaskRepository();
    private InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();

    private CreateRecurringTask createRecurringTask = new CreateRecurringTask(recurringTaskRepository);
    private LocalDateTime tonightAtMidnight = LocalDateTime.of(2017, 11, 6, 0, 0);

    private SpawnRecurringTask spawnRecurringTask = new SpawnRecurringTask(
            recurringTaskRepository,
            taskRepository,
            (lastOccurrence, cadence) -> Stream.of(tonightAtMidnight).iterator()
    );

    private GetPlayerTasks getPlayerTasks = new GetPlayerTasks(
            taskRepository,
            recurringTaskRepository
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

        String expectedResult = randomString(10);

        String result = getPlayerTasks.perform(
                recurringTask.playerId(),
                new GetPlayerTasks.Outcome<String>() {
                    @Override
                    public String foundTasks(List<SavedTask> tasks,
                                             List<SavedRecurringTask> recurringTasks) {
                        assertThat(tasks, hasItem(spawnedTask));
                        assertThat(recurringTasks, hasItem(recurringTask));
                        return expectedResult;
                    }

                });

        assertThat(result, equalTo(expectedResult));
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
