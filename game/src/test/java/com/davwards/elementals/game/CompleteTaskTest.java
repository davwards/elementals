package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.exceptions.NoSuchTaskException;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;
import org.junit.Test;

import java.util.function.Function;

import static com.davwards.elementals.TestUtils.assertThatValueChanges;
import static com.davwards.elementals.TestUtils.assertThatValueIncreases;
import static java.util.function.Function.identity;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompleteTaskTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private TaskRepository taskRepository = new InMemoryTaskRepository();
    private CompleteTask completeTask = new CompleteTask(taskRepository, playerRepository);

    private SavedPlayer existingPlayer = playerRepository.save(new UnsavedPlayer("test-player"));

    @Test
    public void whenTaskExists_marksTaskComplete() {
        SavedTask task = taskRepository.save(
                new UnsavedTask(existingPlayer.getId(), "test task", Task.Status.INCOMPLETE)
        );

        assertThatValueChanges(
                () -> taskRepository.find(task.getId()).get().isComplete(),
                false,
                true,
                () -> completeTask.perform(task.getId(), identity(), () -> null)
        );
    }

    @Test
    public void whenTaskExists_returnsResultOfSuccessMapper() {
        SavedTask task = taskRepository.save(
                new UnsavedTask(existingPlayer.getId(), "test task", Task.Status.INCOMPLETE)
        );

        SavedTask updatedTask = completeTask.perform(
                task.getId(),
                identity(),
                () -> null
        );

        assertThat(updatedTask.getId(), equalTo(task.getId()));
        assertThat(updatedTask.isComplete(), equalTo(true));
    }

    @Test
    public void whenTaskExists_awardsPlayerExperience() {
        SavedTask task = taskRepository.save(
                new UnsavedTask(existingPlayer.getId(), "test task", Task.Status.INCOMPLETE)
        );

        assertThatValueIncreases(
                () -> playerRepository.find(existingPlayer.getId()).get().getExperience(),
                () -> completeTask.perform(task.getId(), identity(), () -> null)
        );
    }

    @Test
    public void whenTaskDoesNotExist_returnsResultOfNoSuchTodoMapper() {
        String result = completeTask.perform(new TaskId("no-such-id"), task -> "task was updated", () -> "no such task");
        assertThat(result, equalTo("no such task"));
    }

}