package com.davwards.elementals.game;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.tasks.SavedTask;
import com.davwards.elementals.game.entities.tasks.TaskRepository;
import com.davwards.elementals.game.fakeplugins.FakeNotifier;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryTaskRepository;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Function;

import static com.davwards.elementals.TestUtils.*;
import static java.util.function.Function.identity;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsEqual.equalTo;

public class MainWorkflowTest {

    private final TaskRepository taskRepository = new InMemoryTaskRepository();
    private final PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private final FakeNotifier notifier = new FakeNotifier();

    private final CreatePlayer createPlayer = new CreatePlayer(playerRepository);
    private final CreateTask createTask = new CreateTask(taskRepository);
    private final CompleteTask completeTask = new CompleteTask(taskRepository, playerRepository);
    private final UpdateTaskStatus updateTaskStatus = new UpdateTaskStatus(taskRepository, playerRepository);
    private final ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository, notifier);

    private SavedPlayer player = createPlayer.perform("testplayer", identity());

    private final LocalDateTime now = LocalDateTime.of(2015, 3, 2, 16, 42, 55);
    private final LocalDateTime tomorrow = now.plusDays(1);
    private final LocalDateTime nextWeek = now.plusDays(7);

    @Test
    public void creatingAndCompletingTasks() {
        SavedTask takeOutTrash = createTask.perform(player.getId(), "Take out trash", tomorrow);
        SavedTask understandRelativity = createTask.perform(player.getId(), "Understand relativity", nextWeek);

        playerGainsExperienceForCompletingATask(takeOutTrash);

        playerDoesNotTakeDamageForTasksThatArentDueOrWereCompleted(tomorrow, takeOutTrash, understandRelativity);

        playerDoesNotTakeDamageForTasksThatArentDueOrWereCompleted(nextWeek, takeOutTrash);

        playerTakesDamageForTasksThatWerentDoneByDeadline(nextWeek, understandRelativity);

        playerDiesAndIsResurrectedAfterTakingTooMuchDamage();
    }

    private void playerDiesAndIsResurrectedAfterTakingTooMuchDamage() {
        int missedTasks = 0;
        while(currentPlayerHealth() > 0) {
            missedTasks++;
            if(missedTasks > 100) {
                fail("Player has missed " + missedTasks + " tasks and hasn't died, something's probably wrong");
            }

            SavedTask task = createTask.perform(player.getId(), "Missed task #" + missedTasks, tomorrow);
            updateTaskStatus.perform(task.getId(), nextWeek);
        }

        assertThat(notifier.notificationsSent().size(), equalTo(0));

        assertThatValueDecreases(
                this::currentPlayerExperience,
                () -> resurrectPlayer.perform(player.getId())
        );

        assertThat(notifier.notificationsSent().size(), equalTo(1));

        assertThat(currentPlayerHealth(), greaterThan(0));
    }

    private void playerDoesNotTakeDamageForTasksThatArentDueOrWereCompleted(LocalDateTime currentTime, SavedTask... tasks) {
        Arrays.stream(tasks).forEach(task ->
            assertThatValueDoesNotChange(
                    this::currentPlayerHealth,
                    () -> updateTaskStatus.perform(task.getId(), currentTime.plusMinutes(2))
            )
        );
    }

    private void playerTakesDamageForTasksThatWerentDoneByDeadline(LocalDateTime currentTime, SavedTask... tasks) {
        Arrays.stream(tasks).forEach(task ->
                assertThatValueDecreases(
                        this::currentPlayerHealth,
                        () -> updateTaskStatus.perform(task.getId(), currentTime.plusMinutes(2))
                )
        );
    }

    private void playerGainsExperienceForCompletingATask(SavedTask takeOutTrash) {
        assertThatValueIncreases(
                this::currentPlayerExperience,
                () -> completeTask.perform(takeOutTrash.getId(), identity(), () -> null)
        );
    }

    private Integer currentPlayerHealth() {
        return playerRepository.find(player.getId()).orElse(null).getHealth();
    }

    private Integer currentPlayerExperience() {
        return playerRepository.find(player.getId()).orElse(null).getExperience();
    }
}
