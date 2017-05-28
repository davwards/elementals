package com.davwards.elementals.game;

import com.davwards.elementals.game.fakes.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakes.InMemoryTaskRepository;
import com.davwards.elementals.game.players.PlayerRepository;
import com.davwards.elementals.game.players.SavedPlayer;
import com.davwards.elementals.game.tasks.*;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Supplier;

import static com.davwards.elementals.TestUtils.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class UpdateTaskStatusTest {

    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private UpdateTaskStatus updateTaskStatus = new UpdateTaskStatus(taskRepository, playerRepository);
    private SavedPlayer player = playerRepository.save(randomUnsavedPlayer());

    private LocalDateTime currentTime = LocalDateTime.of(2016, 11, 5, 14, 35, 59);

    private SavedTask incompleteTaskDueLater = taskRepository.save(
            ImmutableUnsavedTask.builder()
                    .playerId(player.getId())
                    .title("Incomplete task due later")
                    .status(Task.Status.INCOMPLETE)
                    .deadline(currentTime.plusMinutes(5))
                    .build());

    private SavedTask completeTaskDueLater = taskRepository.save(
            ImmutableUnsavedTask.builder()
                    .playerId(player.getId())
                    .title("Complete task due later")
                    .status(Task.Status.COMPLETE)
                    .deadline(currentTime.plusMinutes(5))
                    .build());

    private SavedTask incompleteTaskDueNow = taskRepository.save(
            ImmutableUnsavedTask.builder()
                    .playerId(player.getId())
                    .title("Incomplete task due now")
                    .status(Task.Status.INCOMPLETE)
                    .deadline(currentTime)
                    .build());

    private SavedTask completeTaskDueNow = taskRepository.save(
            ImmutableUnsavedTask.builder()
                    .playerId(player.getId())
                    .title("Complete task due now")
                    .status(Task.Status.COMPLETE)
                    .deadline(currentTime)
                    .build());

    private SavedTask incompleteTaskDueEarlier = taskRepository.save(
            ImmutableUnsavedTask.builder()
                    .playerId(player.getId())
                    .title("Incomplete task due earlier")
                    .status(Task.Status.INCOMPLETE)
                    .deadline(currentTime.minusMinutes(5))
                    .build());

    private SavedTask completeTaskDueEarlier = taskRepository.save(
            ImmutableUnsavedTask.builder()
                    .playerId(player.getId())
                    .title("Complete task due earlier")
                    .status(Task.Status.COMPLETE)
                    .deadline(currentTime.minusMinutes(5))
                    .build());

    private SavedTask pastDueTaskDueEarlier = taskRepository.save(
            ImmutableUnsavedTask.builder()
                    .playerId(player.getId())
                    .title("Past due task due earlier")
                    .status(Task.Status.PAST_DUE)
                    .deadline(currentTime.minusMinutes(5))
                    .build());

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_doesNotChangeStatus() throws Exception {
        Arrays.asList(
                incompleteTaskDueLater,
                completeTaskDueLater,
                incompleteTaskDueNow,
                incompleteTaskDueNow
        ).forEach(task ->
                assertThatValue(statusOf(task))
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
        assertThatValue(
                statusOf(incompleteTaskDueEarlier)
        ).changesFrom(Task.Status.INCOMPLETE, Task.Status.PAST_DUE).when(
                useCaseRunsOn(incompleteTaskDueEarlier)
        );

        assertThatValue(statusOf(completeTaskDueEarlier))
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
                assertThatValue(healthOf(player))
                        .doesNotChangeWhen(useCaseRunsOn(completeTaskDueNow))
        );
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_damagesPlayerForIncompleteTasks() throws Exception {
        assertThatInteger(healthOf(player))
                .decreasesWhen(useCaseRunsOn(incompleteTaskDueEarlier));

        assertThatValue(healthOf(player))
                .doesNotChangeWhen(useCaseRunsOn(completeTaskDueEarlier));

        assertThatValue(healthOf(player))
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
        String expectedResult = randomString(10);
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