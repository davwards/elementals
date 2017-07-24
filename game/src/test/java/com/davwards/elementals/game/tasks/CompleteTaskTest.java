package com.davwards.elementals.game.tasks;

import com.davwards.elementals.players.UpdatePlayerCurrencies;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.players.persistence.PlayerRepository;
import com.davwards.elementals.support.test.Assertions;
import com.davwards.elementals.support.test.Factories;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.Task;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import org.junit.Test;

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
    private UpdatePlayerCurrencies updatePlayerCurrencies = new UpdatePlayerCurrencies(playerRepository);
    private CompleteTask completeTask = new CompleteTask(taskRepository, updatePlayerCurrencies);

    private SavedPlayer existingPlayer = playerRepository.save(Factories.randomUnsavedPlayer());
    private final SavedTask existingTask = taskRepository.save(
            Factories.randomUnsavedTask()
                    .withPlayerId(existingPlayer.getId())
                    .withTitle("test task")
                    .withStatus(Task.Status.INCOMPLETE)
    );

    @Test
    public void whenTaskExists_marksTaskComplete() {
        Assertions.assertThatValue(
                () -> taskRepository.find(existingTask.getId()).get().isComplete())
                .changesFrom(false, true)
                .when(() -> completeTask.perform(
                        existingTask.getId(),
                        noopCompleteTaskResult)
                );
    }

    @Test
    public void whenTaskExists_returnsResultOfSuccessMapper() {
        SavedTask updatedTask = completeTask.perform(
                existingTask.getId(),
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

        assertThat(updatedTask.getId(), equalTo(existingTask.getId()));
        assertThat(updatedTask.isComplete(), equalTo(true));
    }

    @Test
    public void whenTaskExists_awardsPlayerExperience() {
        Assertions.assertThatInteger(() ->
                playerRepository
                        .find(existingPlayer.getId()).get()
                        .experience()
        ).increasesWhen(() ->
                completeTask.perform(
                        existingTask.getId(),
                        noopCompleteTaskResult
                )
        );
    }

    @Test
    public void whenTaskExists_awardsPlayerCoin() {
        Assertions.assertThatInteger(() ->
                playerRepository
                        .find(existingPlayer.getId()).get()
                        .coin()
        ).increasesWhen(() ->
                completeTask.perform(
                        existingTask.getId(),
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