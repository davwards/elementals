package com.davwards.elementals.game;

import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.players.SavedPlayer;
import com.davwards.elementals.game.tasks.*;
import org.junit.Test;

import static com.davwards.elementals.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;

public class CompleteTaskTest {

    private final CompleteTask.Outcome<Void> noopCompleteTaskResult = new CompleteTask.Outcome<Void>() {
        @Override
        public Void taskSuccessfullyCompleted(SavedTask completedTask) {
            return null;
        }

        @Override
        public Void noSuchTask() {
            return null;
        }
    };

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
                .when(() -> completeTask.perform(
                        task.getId(),
                        noopCompleteTaskResult)
                );
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
                new CompleteTask.Outcome<SavedTask>() {
                    @Override
                    public SavedTask taskSuccessfullyCompleted(SavedTask completedTask) {
                        return completedTask;
                    }

                    @Override
                    public SavedTask noSuchTask() {
                        fail("Expected taskSuccessfullyCompleted outcome");
                        return null;
                    }
                }
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
                        noopCompleteTaskResult
                )
        );
    }

    @Test
    public void whenTaskDoesNotExist_returnsResultOfNoSuchTodoMapper() {
        String result = completeTask.perform(
                new TaskId("no-such-id"),
                new CompleteTask.Outcome<String>() {
                    @Override
                    public String taskSuccessfullyCompleted(SavedTask completedTask) {
                        fail("Expected noSuchTask outcome");
                        return null;
                    }

                    @Override
                    public String noSuchTask() {
                        return "no such task";
                    }
                });

        assertThat(result, equalTo("no such task"));
    }

}