package com.davwards.elementals.game;

import com.davwards.elementals.game.notification.fakes.FakeNotifier;
import com.davwards.elementals.game.players.CreatePlayer;
import com.davwards.elementals.game.players.ResurrectPlayer;
import com.davwards.elementals.game.players.UpdatePlayerCurrencies;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.tasks.CompleteTask;
import com.davwards.elementals.game.tasks.CreateTask;
import com.davwards.elementals.game.tasks.UpdateTaskStatus;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.davwards.elementals.game.support.test.Assertions.assertThatInteger;
import static com.davwards.elementals.game.support.test.Factories.randomString;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class MainWorkflowTest {

    @Test
    public void completingATask() {
        givenAPlayer();
        SavedTask task = givenATaskThatIsNotYetDue();

        playerGainsExperienceForCompletingATask(task);
    }

    @Test
    public void notCompletingATask() {
        givenAPlayer();
        SavedTask task = givenATaskThatIsPastDue();

        playerTakesDamageForExpiredTask(task);
    }

    @Test
    public void playerDiesAndIsResurrectedAfterTakingTooMuchDamage() {
        givenAPlayerWithExperience();

        whenPlayerMissesTooManyTasks();

        assertThat(notifier.notificationsSent().size(), equalTo(0));

        assertThatInteger(this::currentPlayerExperience)
                .decreasesWhen(() -> new ResurrectPlayer(playerRepository, notifier).perform(player.getId(), noopResurrectPlayerOutcome));

        assertThat(notifier.notificationsSent().size(), equalTo(1));

        assertThat(currentPlayerHealth(), greaterThan(0));
    }

    private void whenPlayerMissesTooManyTasks() {
        int missedTasks = 0;
        while (currentPlayerHealth() > 0) {
            missedTasks++;
            if (missedTasks > 100) {
                fail("Player has missed " + missedTasks + " tasks and hasn't died, something's probably wrong");
            }

            SavedTask task = new CreateTask(taskRepository).perform(
                    player.getId(),
                    "Missed task #" + missedTasks,
                    currentTime.minusDays(1),
                    getCreatedTask
            );

            new UpdateTaskStatus(taskRepository, playerRepository).perform(
                    task.getId(),
                    currentTime,
                    noopUpdateTaskStatusOutcome
            );
        }
    }

    private void playerTakesDamageForExpiredTask(SavedTask task) {
        assertThatInteger(this::currentPlayerHealth)
                .decreasesWhen(
                        () -> new UpdateTaskStatus(taskRepository, playerRepository).perform(
                                task.getId(),
                                currentTime,
                                noopUpdateTaskStatusOutcome)
                );
    }

    private SavedTask givenATaskThatIsPastDue() {
        return new CreateTask(taskRepository).perform(
                player.getId(),
                "Test Task " + randomString(5),
                currentTime.minusDays(1),
                getCreatedTask
        );
    }

    private SavedTask givenATaskThatIsNotYetDue() {
        return new CreateTask(taskRepository).perform(
                player.getId(),
                "Test Task " + randomString(5),
                currentTime.plusDays(1),
                getCreatedTask
        );
    }

    private void givenAPlayer() {
        player = new CreatePlayer(playerRepository)
                .perform("testplayer", createdPlayer -> createdPlayer);
    }

    private void givenAPlayerWithExperience() {
        player = new CreatePlayer(playerRepository)
                .perform("testplayer", createdPlayer -> createdPlayer);
        SavedTask task = new CreateTask(taskRepository)
                .perform(player.getId(), "Some completed task", getCreatedTask);
        completeTask.perform(task.getId(), noopCompleteTaskResult);
    }

    private void playerGainsExperienceForCompletingATask(SavedTask takeOutTrash) {
        assertThatInteger(this::currentPlayerExperience)
                .increasesWhen(
                        () -> completeTask.perform(
                                takeOutTrash.getId(),
                                noopCompleteTaskResult
                        )
                );
    }

    private Integer currentPlayerHealth() {
        return playerRepository.find(player.getId()).orElse(null).health();
    }

    private Integer currentPlayerExperience() {
        return playerRepository.find(player.getId()).orElse(null).experience();
    }

    private final LocalDateTime currentTime = LocalDateTime.of(2015, 3, 2, 16, 42, 55);
    private SavedPlayer player;

    private final TaskRepository taskRepository = new InMemoryTaskRepository();
    private final PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private final CompleteTask completeTask = new CompleteTask(
            taskRepository,
            new UpdatePlayerCurrencies(playerRepository)
    );
    private final FakeNotifier notifier = new FakeNotifier();

    private final CreateTask.Outcome<SavedTask> getCreatedTask = createdTask -> createdTask;

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

    private final ResurrectPlayer.Outcome<Void> noopResurrectPlayerOutcome = new ResurrectPlayer.Outcome<Void>() {
        @Override
        public Void noSuchPlayer() {
            return null;
        }

        @Override
        public Void playerWasResurrected(SavedPlayer updatedPlayer) {
            return null;
        }

        @Override
        public Void playerDidNotNeedToBeResurrected(SavedPlayer player) {
            return null;
        }
    };

    private final UpdateTaskStatus.Outcome<Void> noopUpdateTaskStatusOutcome = new UpdateTaskStatus.Outcome<Void>() {
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
}
