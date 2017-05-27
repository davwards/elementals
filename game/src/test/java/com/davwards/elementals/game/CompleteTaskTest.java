package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.tasks.*;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.assertThatInteger;
import static com.davwards.elementals.TestUtils.assertThatValue;
import static java.util.function.Function.identity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompleteTaskTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
    private CompleteTask completeTask = new CompleteTask(taskRepository, playerRepository);

    private SavedPlayer existingPlayer = playerRepository.save(new UnsavedPlayer("test-player"));

    @Test
    public void whenTaskExists_marksTaskComplete() {
        SavedTask task = taskRepository.save(
                ImmutableUnsavedTask.builder()
                        .playerId(existingPlayer.getId())
                        .title("test task")
                        .status(Task.Status.INCOMPLETE)
                        .build()
        );

        assertThatValue(() -> taskRepository.find(task.getId()).get().isComplete())
                .changesFrom(false, true)
                .when(() -> completeTask.perform(task.getId(), identity(), () -> null));
    }

    @Test
    public void whenTaskExists_returnsResultOfSuccessMapper() {
        SavedTask task = taskRepository.save(
                ImmutableUnsavedTask.builder()
                        .playerId(existingPlayer.getId())
                        .title("test task")
                        .status(Task.Status.INCOMPLETE)
                        .build()
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
                ImmutableUnsavedTask.builder()
                        .playerId(existingPlayer.getId())
                        .title("test task")
                        .status(Task.Status.INCOMPLETE)
                        .build()
        );

        assertThatInteger(() ->
                playerRepository
                        .find(existingPlayer.getId()).get()
                        .getExperience()
        ).increasesWhen(() ->
                completeTask.perform(
                        task.getId(),
                        identity(),
                        () -> null
                )
        );
    }

    @Test
    public void whenTaskDoesNotExist_returnsResultOfNoSuchTodoMapper() {
        String result = completeTask.perform(new TaskId("no-such-id"), task -> "task was updated", () -> "no such task");
        assertThat(result, equalTo("no such task"));
    }

}