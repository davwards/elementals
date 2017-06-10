package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.tasks.models.RecurringTaskId;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.InMemoryRecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;
import com.davwards.elementals.game.tasks.recurring.CadenceInterpreter;
import com.davwards.elementals.game.tasks.recurring.SpawnRecurringTask;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

import static com.davwards.elementals.game.support.test.Factories.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SpawnRecurringTaskTest {

    private LocalDateTime currentTime = LocalDateTime.now();
    private ControlledTimeProvider timeProvider = new ControlledTimeProvider();
    private SpyCadenceInterpreter spyCadenceInterpreter = new SpyCadenceInterpreter();

    private RecurringTaskRepository recurringTaskRepository =
            new InMemoryRecurringTaskRepository(timeProvider::get);

    private TaskRepository taskRepository =
            new InMemoryTaskRepository(timeProvider::get);

    private SpawnRecurringTask spawnRecurringTask = new SpawnRecurringTask(
            recurringTaskRepository,
            taskRepository,
            spyCadenceInterpreter
    );

    private SavedRecurringTask existingRecurringTask;

    @Test
    public void whenRecurringTaskDoesNotExist_returnsNoSuchRecurringTaskOutcome() throws Exception {
        String expectedResult = randomString(10);

        String result = spawnRecurringTask.perform(
                new RecurringTaskId("nonsense"),
                LocalDateTime.now(),
                new SpawnRecurringTask.Outcome<String>() {
                    @Override
                    public String spawnedNewTask(SavedTask task) {
                        fail("Expected noSuchRecurringTask outcome");
                        return null;
                    }

                    @Override
                    public String taskNotDueToSpawn() {
                        fail("Expected noSuchRecurringTask outcome");
                        return null;
                    }

                    @Override
                    public String noSuchRecurringTask() {
                        return expectedResult;
                    }
                }
        );

        assertThat(result, equalTo(expectedResult));
    }

    @Test
    public void whenRecurringTaskDoesExist_callsInterpreterWithRecurringTaskCadenceAndCreationTime() throws Exception {
        String cadence = "the-cadence-" + randomString(6);
        LocalDateTime creationTime = LocalDateTime.of(
                randomInt(2015, 2019),
                randomInt(1, 12),
                randomInt(1, 28),
                0, 0
        );
        timeProvider.now = creationTime;

        SavedRecurringTask recurringTask = recurringTaskRepository.save(
                randomUnsavedRecurringTask()
                        .withCadence(cadence)
        );

        spawnRecurringTask.perform(
                recurringTask.getId(),
                creationTime.plusDays(7),
                noopSpawnRecurringTaskOutcome
        );

        assertThat(spyCadenceInterpreter.numberOfCalls, equalTo(1));
        assertThat(spyCadenceInterpreter.receivedCadence, equalTo(cadence));
        assertThat(spyCadenceInterpreter.receivedLastOccurrence, equalTo(creationTime));
    }

    @Test
    public void whenOccurrenceStreamIsEmpty_returnsTaskNotDueToSpawnOutcome() throws Exception {
        givenARecurringTaskExists();
        givenTheStreamOfDatesFromTheCadenceInterpreterIs(Stream.empty());
        thenTheUseCaseReturnsTheTaskNotDueToSpawnOutcome();
    }

    @Test
    public void whenOccurrenceStreamIsEntirelyInTheFuture_returnsTaskNotDueToSpawnOutcome() throws Exception {
        givenARecurringTaskExists();
        givenTheStreamOfDatesFromTheCadenceInterpreterIs(
                Stream.iterate(
                        currentTime.plusDays(1), time -> time.plusDays(1)
                )
        );

        thenTheUseCaseReturnsTheTaskNotDueToSpawnOutcome();
    }

    @Test
    public void whenATaskAlreadyExistsAtTheLatestNonfutureOccurrence_returnsTaskNotDueToSpawnOutcome() throws Exception {
        givenARecurringTaskExists();
        givenTheStreamOfDatesFromTheCadenceInterpreterIs(
                Stream.iterate(
                        currentTime.minusHours(36), time -> time.plusHours(24)
                )
        );
        givenInstancesOfTheRecurringTaskOccurredAt(
                currentTime.minusHours(36),
                currentTime.minusHours(12)
        );

        thenTheUseCaseReturnsTheTaskNotDueToSpawnOutcome();
    }

    @Test
    public void whenATaskAlreadyExistsAfterTheLatestNonfutureOccurrence_returnsTaskNotDueToSpawnOutcome() throws Exception {
        givenARecurringTaskExists();
        givenTheStreamOfDatesFromTheCadenceInterpreterIs(Stream.iterate(
                currentTime.minusHours(36), time -> time.plusHours(24)
        ));
        givenInstancesOfTheRecurringTaskOccurredAt(
                currentTime.minusHours(36).plusMinutes(5),
                currentTime.minusHours(12).plusMinutes(5)
        );

        thenTheUseCaseReturnsTheTaskNotDueToSpawnOutcome();
    }

    @Test
    public void whenATaskDoesNotExistAtTheLatestNonfutureOccurrence_spawnsANewTask() {
        givenARecurringTaskExists();
        givenTheStreamOfDatesFromTheCadenceInterpreterIs(Stream.iterate(
                currentTime.minusHours(36), time -> time.plusHours(24)
        ));
        givenInstancesOfTheRecurringTaskOccurredAt(
                currentTime.minusHours(36).plusMinutes(5)
        );

        thenTheUseCaseSpawnsANewTask();
    }

    private void thenTheUseCaseSpawnsANewTask() {
        SavedTask actualResult = spawnRecurringTask.perform(
                existingRecurringTask.getId(),
                currentTime,
                new SpawnRecurringTask.Outcome<SavedTask>() {
                    @Override
                    public SavedTask spawnedNewTask(SavedTask task) {
                        return task;
                    }

                    @Override
                    public SavedTask taskNotDueToSpawn() {
                        fail("Expected spawnedNewTask outcome");
                        return null;
                    }

                    @Override
                    public SavedTask noSuchRecurringTask() {
                        fail("Expected spawnedNewTask outcome");
                        return null;
                    }
                }
        );

        SavedTask fetchedTask = taskRepository.find(actualResult.getId()).get();
        assertThat(fetchedTask, equalTo(actualResult));
        assertThat(fetchedTask.parentRecurringTaskId().get(), equalTo(existingRecurringTask.getId()));
    }

    private void givenARecurringTaskExists() {
        existingRecurringTask
                = recurringTaskRepository.save(randomUnsavedRecurringTask());
    }

    private void givenInstancesOfTheRecurringTaskOccurredAt(LocalDateTime... times) {
        Arrays.stream(times).forEach(time -> {
            timeProvider.now = time;
            taskRepository.save(randomUnsavedTask()
                    .withParentRecurringTaskId(existingRecurringTask.getId())
            );
        });
    }

    private void givenTheStreamOfDatesFromTheCadenceInterpreterIs(Stream<LocalDateTime> stream) {
        spawnRecurringTask = new SpawnRecurringTask(
                recurringTaskRepository,
                taskRepository,
                (last, cadence) -> stream.iterator()
        );
    }

    private void thenTheUseCaseReturnsTheTaskNotDueToSpawnOutcome() {
        String expectedResult = randomString(10);

        String actualResult = spawnRecurringTask.perform(
                existingRecurringTask.getId(),
                currentTime,
                expectTaskNotDueToSpawnOutcome(expectedResult)
        );

        assertThat(actualResult, equalTo(expectedResult));
    }

    private SpawnRecurringTask.Outcome<String> expectTaskNotDueToSpawnOutcome(final String expectedResult) {
        return new SpawnRecurringTask.Outcome<String>() {
            @Override
            public String spawnedNewTask(SavedTask task) {
                fail("Expected taskNotDueToSpawn outcome");
                return null;
            }

            @Override
            public String taskNotDueToSpawn() {
                return expectedResult;
            }

            @Override
            public String noSuchRecurringTask() {
                fail("Expected taskNotDueToSpawn outcome");
                return null;
            }
        };
    }

    private class SpyCadenceInterpreter implements CadenceInterpreter {
        LocalDateTime receivedLastOccurrence = null;
        String receivedCadence = null;
        Integer numberOfCalls = 0;

        @Override
        public Iterator<LocalDateTime> nextOccurrences(LocalDateTime lastOccurrence, String cadence) {
            receivedCadence = cadence;
            receivedLastOccurrence = lastOccurrence;
            numberOfCalls++;

            return new ArrayList<LocalDateTime>().iterator();
        }

    }

    private class ControlledTimeProvider {
        LocalDateTime now = currentTime;

        LocalDateTime get() {
            return now;
        }
    }

    private SpawnRecurringTask.Outcome<Void> noopSpawnRecurringTaskOutcome = new SpawnRecurringTask.Outcome<Void>() {
        @Override
        public Void spawnedNewTask(SavedTask task) {
            return null;
        }

        @Override
        public Void taskNotDueToSpawn() {
            return null;
        }

        @Override
        public Void noSuchRecurringTask() {
            return null;
        }
    };
}