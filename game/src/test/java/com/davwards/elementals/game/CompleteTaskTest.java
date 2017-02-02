package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.assertThatValueChanges;
import static com.davwards.elementals.TestUtils.assertThatValueIncreases;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompleteTaskTest {

    PlayerRepository playerRepository = new InMemoryPlayerRepository();
    TaskRepository taskRepository = new InMemoryTaskRepository();
    CompleteTask completeTask = new CompleteTask(taskRepository, playerRepository);

    SavedPlayer existingPlayer = playerRepository.save(new UnsavedPlayer("test-player"));

    @Test
    public void whenTaskExists_marksTaskComplete() {
        SavedTask task = taskRepository.save(
                new UnsavedTask(existingPlayer.getId(), "test task", Task.Status.INCOMPLETE)
        );

        assertThatValueChanges(
                () -> taskRepository.find(task.getId()).get().isComplete(),
                false,
                true,
                () -> completeTask.perform(task.getId())
        );
    }

    @Test
    public void whenTaskExists_awardsPlayerExperience() {
        SavedTask task = taskRepository.save(
                new UnsavedTask(existingPlayer.getId(), "test task", Task.Status.INCOMPLETE)
        );

        assertThatValueIncreases(
                () -> playerRepository.find(existingPlayer.getId()).get().getExperience(),
                () -> completeTask.perform(task.getId())
        );
    }

    @Test
    public void whenTaskDoesNotExist_throwsException() {
        try{
            completeTask.perform(new TaskId("no-such-id"));
            fail("Expected a NoSuchTaskException to be thrown");
        } catch(NoSuchTaskException e) {
            assertThat(e.getTaskId(), equalTo(new TaskId("no-such-id")));
        }
    }

}