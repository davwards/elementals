package com.davwards.elementals.game.entities;

import com.davwards.elementals.game.entities.CrudRepository;
import com.davwards.elementals.game.entities.SavedEntity;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

public abstract class CrudRepositoryTest<R extends CrudRepository<U, S, I>, I, U, S extends SavedEntity<I>> {

    protected abstract R repository();
    protected abstract U givenAnUnsavedRecord();
    protected abstract S whenASavedRecordIsModified(S original);
    protected abstract void assertIdentical(U original, S saved);
    protected abstract void assertIdentical(S original, S saved);
    protected abstract void assertNotIdentical(S left, S right);

    @Test
    public void saveAndFetch() throws Exception {
        U unsavedRecord1 = givenAnUnsavedRecord();
        U unsavedRecord2 = givenAnUnsavedRecord();

        S savedTask1 = repository().save(unsavedRecord1);
        S savedTask2 = repository().save(unsavedRecord2);

        assertThat(savedTask1.getId(), not(equalTo(savedTask2.getId())));

        assertIdentical(unsavedRecord1, repository().find(savedTask1.getId()).get());
        assertIdentical(unsavedRecord2, repository().find(savedTask2.getId()).get());
    }

    @Test
    public void fetchingReturnsCopiesOfTheEntityFromStorage() throws Exception {
        U unsavedRecord = givenAnUnsavedRecord();
        S savedRecord = repository().save(unsavedRecord);

        assertIdentical(savedRecord, repository().find(savedRecord.getId()).get());
        S modifiedRecord = whenASavedRecordIsModified(savedRecord);
        // changes not saved
        assertNotIdentical(modifiedRecord, repository().find(savedRecord.getId()).get());

        repository().update(modifiedRecord);

        assertIdentical(modifiedRecord, repository().find(savedRecord.getId()).get());
        S twiceModifiedRecord = whenASavedRecordIsModified(modifiedRecord);
        // changes not saved
        assertNotIdentical(repository().find(twiceModifiedRecord.getId()).get(), twiceModifiedRecord);
    }

    @Test
    public void all() throws Exception {
        assertThat(repository().all().size(), equalTo(0));

        U unsavedRecord1 = givenAnUnsavedRecord();
        S savedRecord1 = repository().save(unsavedRecord1);
        assertThat(repository().all().size(), equalTo(1));
        assertThat(repository().all(), hasItem(savedRecord1));

        U unsavedRecord2 = givenAnUnsavedRecord();
        S savedRecord2 = repository().save(unsavedRecord2);
        assertThat(repository().all().size(), equalTo(2));
        assertThat(repository().all(), hasItem(savedRecord2));
    }
}
