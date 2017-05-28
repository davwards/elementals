package com.davwards.elementals.game;

import com.davwards.elementals.game.notification.fakes.FakeNotifier;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.persistence.InMemoryTaskRepository;
import com.davwards.elementals.game.players.CreatePlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import com.davwards.elementals.game.players.ResurrectPlayer;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.tasks.*;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.davwards.elementals.TestUtils.assertThatInteger;
import static com.davwards.elementals.TestUtils.assertThatValue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class MainWorkflowTest {

    @Test
    public void creatingAndCompletingTasks() {
        SavedTask takeOutTrash = createTask.perform(
                player.getId(),
                "Take out trash",
                tomorrow,
                getCreatedTask
        );

        SavedTask understandRelativity = createTask.perform(
                player.getId(),
                "Understand relativity",
                nextWeek,
                getCreatedTask
        );

        playerGainsExperienceForCompletingATask(takeOutTrash);

        playerDoesNotTakeDamageForTasksThatArentDueOrWereCompleted(tomorrow, takeOutTrash, understandRelativity);

        playerDoesNotTakeDamageForTasksThatArentDueOrWereCompleted(nextWeek, takeOutTrash);

        playerTakesDamageForTasksThatWerentDoneByDeadline(nextWeek, understandRelativity);

        playerDiesAndIsResurrectedAfterTakingTooMuchDamage();
    }

    private void playerDiesAndIsResurrectedAfterTakingTooMuchDamage() {
        int missedTasks = 0;
        while (currentPlayerHealth() > 0) {
            missedTasks++;
            if (missedTasks > 100) {
                fail("Player has missed " + missedTasks + " tasks and hasn't died, something's probably wrong");
            }

            SavedTask task = createTask
                    .perform(player.getId(), "Missed task #" + missedTasks, tomorrow, getCreatedTask);
            updateTaskStatus.perform(task.getId(), nextWeek, noopUpdateTaskStatusOutcome);
        }

        assertThat(notifier.notificationsSent().size(), equalTo(0));

        assertThatInteger(this::currentPlayerExperience)
                .decreasesWhen(() -> resurrectPlayer.perform(player.getId(), noopResurrectPlayerOutcome));

        assertThat(notifier.notificationsSent().size(), equalTo(1));

        assertThat(currentPlayerHealth(), greaterThan(0));
    }

    private void playerDoesNotTakeDamageForTasksThatArentDueOrWereCompleted(LocalDateTime currentTime, SavedTask... tasks) {
        Arrays.stream(tasks).forEach(task ->
                assertThatValue(this::currentPlayerHealth).doesNotChangeWhen(
                        () -> updateTaskStatus.perform(task.getId(), currentTime.plusMinutes(2), noopUpdateTaskStatusOutcome)
                )
        );
    }

    private void playerTakesDamageForTasksThatWerentDoneByDeadline(LocalDateTime currentTime, SavedTask... tasks) {
        Arrays.stream(tasks).forEach(task ->
                assertThatInteger(this::currentPlayerHealth)
                        .decreasesWhen(
                                () -> updateTaskStatus.perform(
                                        task.getId(),
                                        currentTime.plusMinutes(2),
                                        noopUpdateTaskStatusOutcome)
                        )
        );
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

    private final LocalDateTime now = LocalDateTime.of(2015, 3, 2, 16, 42, 55);
    private final LocalDateTime tomorrow = now.plusDays(1);
    private final LocalDateTime nextWeek = now.plusDays(7);

    private final TaskRepository taskRepository = new InMemoryTaskRepository();
    private final PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private final FakeNotifier notifier = new FakeNotifier();

    private final CreatePlayer createPlayer = new CreatePlayer(playerRepository);
    private final CreateTask createTask = new CreateTask(taskRepository);
    private final CompleteTask completeTask = new CompleteTask(taskRepository, playerRepository);
    private final UpdateTaskStatus updateTaskStatus = new UpdateTaskStatus(taskRepository, playerRepository);
    private final ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository, notifier);

    private SavedPlayer player = createPlayer
            .perform("testplayer", createdPlayer -> createdPlayer);

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
