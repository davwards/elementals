package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.CrudRepositoryTest;
import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.tasks.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.davwards.elementals.TestUtils.randomInt;
import static com.davwards.elementals.TestUtils.randomString;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class TaskRepositoryTest extends CrudRepositoryTest<TaskRepository, TaskId, UnsavedTask, SavedTask> {
    protected abstract TaskRepository repository();

    private PlayerId playerId = new PlayerId("player-id");

    @Override
    protected UnsavedTask givenAnUnsavedRecord() {
        return new UnsavedTask(
                playerId,
                "Task " + randomString(5),
                Task.Status.INCOMPLETE,
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
    protected void whenASavedRecordIsModified(SavedTask original) {
        original.setTitle("Modified Task " + UUID.randomUUID().toString().substring(0, 5));
    }

    private void assertTaskIsIdentical(Task left, Task right) {
        assertThat(left.getTitle(), equalTo(right.getTitle()));
        assertThat(left.getStatus(), equalTo(right.getStatus()));
        assertThat(left.getDeadline(), equalTo(right.getDeadline()));
        assertThat(left.getPlayerId(), equalTo(right.getPlayerId()));
    }

    @Override
    protected void assertIdentical(UnsavedTask original, SavedTask saved) {
        assertTaskIsIdentical(original, saved);
    }

    @Override
    protected void assertIdentical(SavedTask original, SavedTask saved) {
        assertTaskIsIdentical(original, saved);
    }

    @Override
    protected void assertNotIdentical(SavedTask left, SavedTask right) {
        assertThat(left, not(allOf(
                hasProperty("title", equalTo(right.getTitle())),
                hasProperty("status", equalTo(right.getStatus())),
                hasProperty("deadline", equalTo(right.getDeadline())),
                hasProperty("playerId", equalTo(right.getPlayerId()))
        )));
    }
}