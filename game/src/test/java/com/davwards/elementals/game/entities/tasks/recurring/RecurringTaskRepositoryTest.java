package com.davwards.elementals.game.entities.tasks.recurring;

import com.davwards.elementals.game.entities.CrudRepositoryTest;

import static com.davwards.elementals.TestUtils.randomString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;

public abstract class RecurringTaskRepositoryTest extends
        CrudRepositoryTest<
                RecurringTaskRepository,
                RecurringTaskId,
                UnsavedRecurringTask,
                SavedRecurringTask> {

    @Override
    protected UnsavedRecurringTask givenAnUnsavedRecord() {
        return new UnsavedRecurringTask("Recurring Task: " + randomString(5));
    }

    @Override
    protected SavedRecurringTask whenASavedRecordIsModified(SavedRecurringTask original) {
        original.setTitle("Some new title " + randomString(5));
        return original;
    }

    @Override
    protected void assertIdentical(UnsavedRecurringTask original, SavedRecurringTask saved) {
        assertThat(original.getTitle(), equalTo(saved.getTitle()));
    }

    @Override
    protected void assertIdentical(SavedRecurringTask original, SavedRecurringTask saved) {
        assertThat(original.getTitle(), equalTo(saved.getTitle()));
    }

    @Override
    protected void assertNotIdentical(SavedRecurringTask left, SavedRecurringTask right) {
        assertThat(left.getTitle(), not(equalTo(right.getTitle())));
    }
}