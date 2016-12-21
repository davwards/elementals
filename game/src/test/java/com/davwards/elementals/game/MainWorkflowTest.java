package com.davwards.elementals.game;

import com.davwards.elementals.game.CompleteTodo;
import com.davwards.elementals.game.CreateTodo;
import com.davwards.elementals.game.ResurrectPlayer;
import com.davwards.elementals.game.UpdateTodoStatus;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;
import com.davwards.elementals.game.entities.todos.SavedTodo;
import com.davwards.elementals.game.entities.todos.TodoRepository;
import com.davwards.elementals.game.exceptions.NoSuchTodoException;
import com.davwards.elementals.game.inmemorypersistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.inmemorypersistence.InMemoryTodoRepository;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.davwards.elementals.TestUtils.*;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class MainWorkflowTest {

    private final TodoRepository todoRepository = new InMemoryTodoRepository();
    private final PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private final CreateTodo createTodo = new CreateTodo(todoRepository);
    private final CompleteTodo completeTodo = new CompleteTodo(todoRepository, playerRepository);
    private final UpdateTodoStatus updateTodoStatus = new UpdateTodoStatus(todoRepository, playerRepository);
    private final ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository);

    private SavedPlayer player = playerRepository.save(new UnsavedPlayer("testplayer"));

    private final LocalDateTime now = LocalDateTime.of(2015, 3, 2, 16, 42, 55);
    private final LocalDateTime tomorrow = now.plusDays(1);
    private final LocalDateTime nextWeek = now.plusDays(7);

    @Test
    public void creatingAndCompletingTodos() throws NoSuchTodoException {

        SavedTodo takeOutTrash = createTodo.perform(player.getId(), "Take out trash", tomorrow);
        SavedTodo understandRelativity = createTodo.perform(player.getId(), "Understand relativity", nextWeek);

        playerGainsExperienceForCompletingATodo(takeOutTrash);

        playerDoesNotTakeDamageForTodosThatArentDueOrWereCompleted(tomorrow, takeOutTrash, understandRelativity);

        playerDoesNotTakeDamageForTodosThatArentDueOrWereCompleted(nextWeek, takeOutTrash);

        playerTakesDamageForTodosThatWerentDoneByDeadline(nextWeek, understandRelativity);

        playerDiesAndIsResurrectedAfterTakingTooMuchDamage();
    }

    private void playerDiesAndIsResurrectedAfterTakingTooMuchDamage() {
        int missedTodos = 0;
        while(currentPlayerHealth() > 0) {
            missedTodos++;
            if(missedTodos > 100) {
                fail("Player has missed " + missedTodos + " todos and hasn't died, something's probably wrong");
            }

            SavedTodo todo = createTodo.perform(player.getId(), "Missed todo #" + missedTodos, tomorrow);
            updateTodoStatus.perform(todo.getId(), nextWeek);
        }

        assertThatValueDecreases(
                this::currentPlayerExperience,
                () -> resurrectPlayer.perform(player.getId())
        );

        assertThat(currentPlayerHealth(), greaterThan(0));
    }

    private void playerDoesNotTakeDamageForTodosThatArentDueOrWereCompleted(LocalDateTime currentTime, SavedTodo... todos) {
        Arrays.stream(todos).forEach(todo ->
            assertThatValueDoesNotChange(
                    this::currentPlayerHealth,
                    () -> updateTodoStatus.perform(todo.getId(), currentTime.plusMinutes(2))
            )
        );
    }

    private void playerTakesDamageForTodosThatWerentDoneByDeadline(LocalDateTime currentTime, SavedTodo... todos) {
        Arrays.stream(todos).forEach(todo ->
                assertThatValueDecreases(
                        this::currentPlayerHealth,
                        () -> updateTodoStatus.perform(todo.getId(), currentTime.plusMinutes(2))
                )
        );
    }

    private void playerGainsExperienceForCompletingATodo(SavedTodo takeOutTrash) {
        assertThatValueIncreases(
                this::currentPlayerExperience,
                () -> completeTodo.perform(takeOutTrash.getId())
        );
    }

    private Integer currentPlayerHealth() {
        return playerRepository.find(player.getId()).orElse(null).getHealth();
    }

    private Integer currentPlayerExperience() {
        return playerRepository.find(player.getId()).orElse(null).getExperience();
    }
}
