package com.davwards.elementals.game.todos;

import com.davwards.elementals.game.CrudRepositoryTest;
import com.davwards.elementals.game.players.PlayerId;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.davwards.elementals.TestUtils.randomInt;
import static com.davwards.elementals.TestUtils.randomString;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class TodoRepositoryTest extends CrudRepositoryTest<TodoRepository, TodoId, UnsavedTodo, SavedTodo> {
    protected abstract TodoRepository repository();

    private PlayerId playerId = new PlayerId("player-id");

    @Override
    protected UnsavedTodo givenAnUnsavedRecord() {
        return new UnsavedTodo(
                playerId,
                "Todo " + randomString(5),
                Todo.Status.INCOMPLETE,
                LocalDateTime.of(
                        2016,
                        randomInt(1, 12),
                        randomInt(1, 28),
                        randomInt(0, 23),
                        randomInt(0, 59),
                        randomInt(0, 59)
                )
        );
    }

    @Override
    protected void whenASavedRecordIsModified(SavedTodo original) {
        original.setTitle("Modified Todo " + UUID.randomUUID().toString().substring(0, 5));
    }

    private void assertTodoIsIdentical(Todo left, Todo right) {
        assertThat(left.getTitle(), equalTo(right.getTitle()));
        assertThat(left.getStatus(), equalTo(right.getStatus()));
        assertThat(left.getDeadline(), equalTo(right.getDeadline()));
        assertThat(left.getPlayerId(), equalTo(right.getPlayerId()));
    }

    @Override
    protected void assertIdentical(UnsavedTodo original, SavedTodo saved) {
        assertTodoIsIdentical(original, saved);
    }

    @Override
    protected void assertIdentical(SavedTodo original, SavedTodo saved) {
        assertTodoIsIdentical(original, saved);
    }

    @Override
    protected void assertNotIdentical(SavedTodo left, SavedTodo right) {
        assertThat(left, not(allOf(
                hasProperty("title", equalTo(right.getTitle())),
                hasProperty("status", equalTo(right.getStatus())),
                hasProperty("deadline", equalTo(right.getDeadline())),
                hasProperty("playerId", equalTo(right.getPlayerId()))
        )));
    }
}