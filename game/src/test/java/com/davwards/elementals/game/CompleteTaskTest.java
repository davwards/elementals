package com.davwards.elementals.game;

import com.davwards.elementals.game.players.PlayerRepository;
import com.davwards.elementals.game.players.SavedPlayer;
import com.davwards.elementals.game.fakes.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakes.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.*;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.*;
import static java.util.function.Function.identity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CompleteTaskTest {

    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private InMemoryTaskRepository taskRepository = new InMemoryTaskRepository();
    private CompleteTask completeTask = new CompleteTask(taskRepository, playerRepository);

    private SavedPlayer existingPlayer = playerRepository.save(randomUnsavedPlayer());

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
                        .experience()
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