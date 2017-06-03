package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.support.persistence.CrudRepositoryTest;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedRecurringTask;
import com.davwards.elementals.game.tasks.models.RecurringTaskId;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.models.UnsavedRecurringTask;

import static com.davwards.elementals.game.support.test.Factories.randomUnsavedRecurringTask;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public abstract class RecurringTaskRepositoryTest extends
        CrudRepositoryTest<RecurringTaskRepository, RecurringTaskId, UnsavedRecurringTask, SavedRecurringTask> {
    protected abstract RecurringTaskRepository repository();

    @Override
    protected UnsavedRecurringTask givenAnUnsavedRecord() {
        return randomUnsavedRecurringTask();
    }

    @Override
    protected SavedRecurringTask whenASavedRecordIsModified(SavedRecurringTask original) {
        return SavedRecurringTask.copy(original).withTitle("Updated " + original.title());
    }

    @Override
    protected void assertIdentical(UnsavedRecurringTask original, SavedRecurringTask saved) {
        assertThat(
                original,
                equalTo(ImmutableUnsavedRecurringTask.builder().from(saved).build())
        );
    }

    @Override
    protected void assertIdentical(SavedRecurringTask original, SavedRecurringTask saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedRecurringTask left, SavedRecurringTask right) {
        assertThat(left, not(equalTo(right)));
    }
}
