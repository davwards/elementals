package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.entities.CrudRepositoryTest;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.models.UnsavedTask;

import static com.davwards.elementals.TestUtils.randomUnsavedTask;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public abstract class TaskRepositoryTest extends
        CrudRepositoryTest<TaskRepository, TaskId, UnsavedTask, SavedTask> {
    protected abstract TaskRepository repository();

    @Override
    protected UnsavedTask givenAnUnsavedRecord() {
        return randomUnsavedTask();
    }

    @Override
    protected SavedTask whenASavedRecordIsModified(SavedTask original) {
        return SavedTask.copy(original).withTitle("Updated " + original.title());
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
