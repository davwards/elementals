package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.TaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Supplier;

import static com.davwards.elementals.TestUtils.assertThatInteger;
import static com.davwards.elementals.TestUtils.assertThatValue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class UpdateTaskStatusTest {

    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private UpdateTaskStatus updateTaskStatus = new UpdateTaskStatus(taskRepository, playerRepository);

    private SavedPlayer player = playerRepository.save(new UnsavedPlayer("test-player"));
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
    public void whenTaskDoesNotExist_throwsException() {
        try {
            updateTaskStatus.perform(new TaskId("no-such-id"), currentTime);
            fail("Expected a NoSuchTaskException to be thrown");
        } catch (NoSuchTaskException e) {
            assertThat(e.getTaskId(), equalTo(new TaskId("no-such-id")));
        }
    }

    private Runnable useCaseRunsOn(SavedTask taskOfInterest) {
        return () -> updateTaskStatus.perform(
                taskOfInterest.getId(),
                currentTime
        );
    }

    private Supplier<Task.Status> statusOf(SavedTask taskOfInterest) {
        return () -> taskRepository.find(taskOfInterest.getId()).get().status();
    }

    private Supplier<Integer> healthOf(SavedPlayer playerOfInterest) {
        return () -> playerRepository.find(playerOfInterest.getId()).get().getHealth();
    }
}