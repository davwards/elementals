package com.davwards.elementals.game.tasks;

import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.players.persistence.PlayerRepository;
import com.davwards.elementals.support.test.Assertions;
import com.davwards.elementals.support.test.Factories;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.Task;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Supplier;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class UpdateTaskStatusTest {

    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private UpdateTaskStatus updateTaskStatus = new UpdateTaskStatus(taskRepository, playerRepository);
    private SavedPlayer player = playerRepository.save(Factories.randomUnsavedPlayer());

    private LocalDateTime currentTime = LocalDateTime.of(2016, 11, 5, 14, 35, 59);

    private SavedTask incompleteTaskDueLater = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(player.getId())
                    .withTitle("Incomplete task due later")
                    .withStatus(Task.Status.INCOMPLETE)
                    .withDeadline(currentTime.plusMinutes(5))
    );

    private SavedTask completeTaskDueLater = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(player.getId())
                    .withTitle("Complete task due later")
                    .withStatus(Task.Status.COMPLETE)
                    .withDeadline(currentTime.plusMinutes(5))
    );

    private SavedTask incompleteTaskDueNow = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(player.getId())
                    .withTitle("Incomplete task due now")
                    .withStatus(Task.Status.INCOMPLETE)
                    .withDeadline(currentTime)
    );

    private SavedTask completeTaskDueNow = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(player.getId())
                    .withTitle("Complete task due now")
                    .withStatus(Task.Status.COMPLETE)
                    .withDeadline(currentTime)
    );

    private SavedTask incompleteTaskDueEarlier = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(player.getId())
                    .withTitle("Incomplete task due earlier")
                    .withStatus(Task.Status.INCOMPLETE)
                    .withDeadline(currentTime.minusMinutes(5))
    );

    private SavedTask completeTaskDueEarlier = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(player.getId())
                    .withTitle("Complete task due earlier")
                    .withStatus(Task.Status.COMPLETE)
                    .withDeadline(currentTime.minusMinutes(5))
    );

    private SavedTask pastDueTaskDueEarlier = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(player.getId())
                    .withTitle("Past due task due earlier")
                    .withStatus(Task.Status.PAST_DUE)
                    .withDeadline(currentTime.minusMinutes(5))
    );

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_doesNotChangeStatus() throws Exception {
        Arrays.asList(
                incompleteTaskDueLater,
                completeTaskDueLater,
                incompleteTaskDueNow,
                incompleteTaskDueNow
        ).forEach(task ->
                Assertions.assertThatValue(statusOf(task))
                        .doesNotChangeWhen(useCaseRunsOn(task))
        );
    }

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_returnsNoStatusChangeOutcome() throws Exception {
        Arrays.asList(
                incompleteTaskDueLater,
                completeTaskDueLater,
                incompleteTaskDueNow,
                incompleteTaskDueNow
        ).forEach(task -> {
            SavedTask result = updateTaskStatus.perform(
                    task.getId(),
                    currentTime,
                    expectNoStatusChangeOutcome
            );

            assertThat(result, equalTo(task));
        });
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_changesIncompleteTasksToPastDue() throws Exception {
        Assertions.assertThatValue(
                statusOf(incompleteTaskDueEarlier)
        ).changesFrom(Task.Status.INCOMPLETE, Task.Status.PAST_DUE).when(
                useCaseRunsOn(incompleteTaskDueEarlier)
        );

        Assertions.assertThatValue(statusOf(completeTaskDueEarlier))
                .doesNotChangeWhen(useCaseRunsOn(completeTaskDueEarlier));
    }

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_doesNotDamagePlayer() throws Exception {
        Arrays.asList(
                incompleteTaskDueLater,
                completeTaskDueLater,
                incompleteTaskDueNow,
                incompleteTaskDueNow
        ).forEach(task ->
                Assertions.assertThatValue(healthOf(player))
                        .doesNotChangeWhen(useCaseRunsOn(completeTaskDueNow))
        );
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_damagesPlayerForIncompleteTasks() throws Exception {
        Assertions.assertThatInteger(healthOf(player))
                .decreasesWhen(useCaseRunsOn(incompleteTaskDueEarlier));

        Assertions.assertThatValue(healthOf(player))
                .doesNotChangeWhen(useCaseRunsOn(completeTaskDueEarlier));

        Assertions.assertThatValue(healthOf(player))
                .doesNotChangeWhen(useCaseRunsOn(pastDueTaskDueEarlier));
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_andTaskIsComplete_returnsNoStatusChangeOutcome() throws Exception {
        SavedTask result = updateTaskStatus.perform(
                completeTaskDueEarlier.getId(),
                currentTime,
                expectNoStatusChangeOutcome
        );

        assertThat(result, equalTo(completeTaskDueEarlier));
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_andTaskIsPastDue_returnsNoStatusChangeOutcome() throws Exception {
        SavedTask result = updateTaskStatus.perform(
                pastDueTaskDueEarlier.getId(),
                currentTime,
                expectNoStatusChangeOutcome
        );

        assertThat(result, equalTo(pastDueTaskDueEarlier));
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_andTaskIsIncomplete_returnsTaskExpiredOutcome() throws Exception {
        SavedTask result = updateTaskStatus.perform(
                incompleteTaskDueEarlier.getId(),
                currentTime,
                new UpdateTaskStatus.Outcome<SavedTask>() {
                    @Override
                    public SavedTask noSuchTask() {
                        fail("Expected taskExpired outcome");
                        return null;
                    }

                    @Override
                    public SavedTask taskExpired(SavedTask updatedTask) {
                        return updatedTask;
                    }

                    @Override
                    public SavedTask noStatusChange(SavedTask task) {
                        fail("Expected taskExpired outcome");
                        return null;
                    }
                }
        );

        assertThat(result, equalTo(taskRepository.find(incompleteTaskDueEarlier.getId()).get()));
    }

    @Test
    public void whenTaskDoesNotExist_returnsNoSuchTaskOutcome() {
        String expectedResult = Factories.randomString(10);
        String result = updateTaskStatus.perform(
                new TaskId("no-such-id"),
                currentTime,
                new UpdateTaskStatus.Outcome<String>() {
                    @Override
                    public String noSuchTask() {
                        return expectedResult;
                    }

                    @Override
                    public String taskExpired(SavedTask updatedTask) {
                        fail("Expected noSuchTask outcome");
                        return null;
                    }

                    @Override
                    public String noStatusChange(SavedTask task) {
                        fail("Expected noSuchTask outcome");
                        return null;
                    }
                }
        );

        assertThat(result, equalTo(expectedResult));
    }

    private Runnable useCaseRunsOn(SavedTask taskOfInterest) {
        return () -> updateTaskStatus.perform(
                taskOfInterest.getId(),
                currentTime,
                noopOutcome);
    }

    private Supplier<Task.Status> statusOf(SavedTask taskOfInterest) {
        return () -> taskRepository.find(taskOfInterest.getId()).get().status();
    }

    private Supplier<Integer> healthOf(SavedPlayer playerOfInterest) {
        return () -> playerRepository.find(playerOfInterest.getId()).get().health();
    }

    private final UpdateTaskStatus.Outcome<Void> noopOutcome = new UpdateTaskStatus.Outcome<Void>() {
        @Override
        public Void noSuchTask() {
            return null;
        }

        @Override
        public Void taskExpired(SavedTask updatedTask) {
            return null;
        }

        @Override
        public Void noStatusChange(SavedTask task) {
            return null;
        }
    };

    private final UpdateTaskStatus.Outcome<SavedTask> expectNoStatusChangeOutcome = new UpdateTaskStatus.Outcome<SavedTask>() {
        @Override
        public SavedTask noSuchTask() {
            fail("Expected noStatusChange outcome");
            return null;
        }

        @Override
        public SavedTask taskExpired(SavedTask updatedTask) {
            fail("Expected noStatusChange outcome");
            return null;
        }

        @Override
        public SavedTask noStatusChange(SavedTask task) {
            return task;
        }
    };
}