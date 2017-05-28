package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.entities.CrudRepositoryTest;
import com.davwards.elementals.game.players.PlayerId;
import com.davwards.elementals.game.tasks.*;

import static com.davwards.elementals.TestUtils.randomString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public abstract class TaskRepositoryTest extends
        CrudRepositoryTest<TaskRepository, TaskId, UnsavedTask, SavedTask> {
    protected abstract TaskRepository repository();

    @Override
    protected UnsavedTask givenAnUnsavedRecord() {
        return ImmutableUnsavedTask.builder()
                .playerId(new PlayerId(randomString(10)))
                .title("Test Task " + randomString(10))
                .status(Task.Status.INCOMPLETE)
                .build();
    }

    @Override
    protected SavedTask whenASavedRecordIsModified(SavedTask original) {
        return ImmutableSavedTask.builder()
                .from(original)
                .title("Updated " + original.title())
                .build();
    }

    @Override
    protected void assertIdentical(UnsavedTask original, SavedTask saved) {
        assertThat(original, equalTo(ImmutableUnsavedTask.builder().from(saved).build()));
    }

    @Override
    protected void assertIdentical(SavedTask original, SavedTask saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedTask left, SavedTask right) {
        assertThat(left, not(equalTo(right)));
    }
}
