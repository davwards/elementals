package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.davwards.elementals.TestUtils.assertThatValueChanges;
import static com.davwards.elementals.TestUtils.assertThatValueDecreases;
import static com.davwards.elementals.TestUtils.assertThatValueDoesNotChange;
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
            new UnsavedTask(player.getId(), "Incomplete task due later", Task.Status.INCOMPLETE, currentTime.plusMinutes(5)));
    private SavedTask completeTaskDueLater = taskRepository.save(
            new UnsavedTask(player.getId(), "Complete task due later", Task.Status.COMPLETE, currentTime.plusMinutes(5)));

    private SavedTask incompleteTaskDueNow = taskRepository.save(
            new UnsavedTask(player.getId(), "Incomplete task due now", Task.Status.INCOMPLETE, currentTime));
    private SavedTask completeTaskDueNow = taskRepository.save(
            new UnsavedTask(player.getId(), "Complete task due now", Task.Status.COMPLETE, currentTime));

    private SavedTask incompleteTaskDueEarlier = taskRepository.save(
            new UnsavedTask(player.getId(), "Incomplete task due earlier", Task.Status.INCOMPLETE, currentTime.minusMinutes(5)));
    private SavedTask completeTaskDueEarlier = taskRepository.save(
            new UnsavedTask(player.getId(), "Complete task due earlier", Task.Status.COMPLETE, currentTime.minusMinutes(5)));
    private SavedTask pastDueTaskDueEarlier = taskRepository.save(
            new UnsavedTask(player.getId(), "Past due task due earlier", Task.Status.PAST_DUE, currentTime.minusMinutes(5)));

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_doesNotChangeStatus() throws Exception {
        assertThatValueDoesNotChange(
                () -> taskRepository.find(incompleteTaskDueLater.getId()).get().getStatus(),
                () -> updateTaskStatus.perform(incompleteTaskDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> taskRepository.find(completeTaskDueLater.getId()).get().getStatus(),
                () -> updateTaskStatus.perform(completeTaskDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> taskRepository.find(incompleteTaskDueNow.getId()).get().getStatus(),
                () -> updateTaskStatus.perform(incompleteTaskDueNow.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> taskRepository.find(completeTaskDueNow.getId()).get().getStatus(),
                () -> updateTaskStatus.perform(completeTaskDueNow.getId(), currentTime)
        );
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_changesIncompleteTasksToPastDue() throws Exception {
        assertThatValueChanges(
                () -> taskRepository.find(incompleteTaskDueEarlier.getId()).get().getStatus(),
                Task.Status.INCOMPLETE,
                Task.Status.PAST_DUE,
                () -> updateTaskStatus.perform(incompleteTaskDueEarlier.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> taskRepository.find(completeTaskDueEarlier.getId()).get().getStatus(),
                () -> updateTaskStatus.perform(completeTaskDueEarlier.getId(), currentTime)
        );
    }

    @Test
    public void whenCurrentTimeIsBeforeOrOnDeadline_doesNotDamagePlayer() throws Exception {
        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTaskStatus.perform(incompleteTaskDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTaskStatus.perform(completeTaskDueLater.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTaskStatus.perform(incompleteTaskDueNow.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTaskStatus.perform(completeTaskDueNow.getId(), currentTime)
        );
    }

    @Test
    public void whenCurrentTimeIsAfterDeadline_damagesPlayerForIncompleteTasks() throws Exception {
        assertThatValueDecreases(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTaskStatus.perform(incompleteTaskDueEarlier.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTaskStatus.perform(completeTaskDueEarlier.getId(), currentTime)
        );

        assertThatValueDoesNotChange(
                () -> playerRepository.find(player.getId()).get().getHealth(),
                () -> updateTaskStatus.perform(pastDueTaskDueEarlier.getId(), currentTime)
        );
    }

    @Test
    public void whenTaskDoesNotExist_throwsException() {
        try{
            updateTaskStatus.perform(new TaskId("no-such-id"), currentTime);
            fail("Expected a NoSuchTaskException to be thrown");
        } catch(NoSuchTaskException e) {
            assertThat(e.getTaskId(), equalTo(new TaskId("no-such-id")));
        }
    }
}