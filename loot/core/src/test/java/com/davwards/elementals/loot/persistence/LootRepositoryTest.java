package com.davwards.elementals.loot.persistence;

import com.davwards.elementals.loot.models.*;
import com.davwards.elementals.loot.persistence.LootRepository;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepositoryTest;
import org.junit.Test;

import java.util.List;

import static com.davwards.elementals.support.test.Factories.randomString;
import static com.davwards.elementals.support.test.Factories.randomUnsavedLoot;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public abstract class LootRepositoryTest extends CrudRepositoryTest<LootRepository, LootId, UnsavedLoot, SavedLoot> {

    @Test
    public void findByPlayerId() throws Exception {
        LootRepository lootRepository = repository();

        SavedLoot matchingLoot1 = lootRepository.save(randomUnsavedLoot()
                .withPlayerId(new PlayerId("matching")));
        SavedLoot matchingLoot2 = lootRepository.save(randomUnsavedLoot()
                .withPlayerId(new PlayerId("matching")));
        lootRepository.save(randomUnsavedLoot()
                .withPlayerId(new PlayerId("nonmatching")));

        List<SavedLoot> fetchedLoot = lootRepository.findByPlayerId(new PlayerId("matching"));

        assertThat(fetchedLoot, containsInAnyOrder(matchingLoot1, matchingLoot2));
    }

    @Override
    protected UnsavedLoot givenAnUnsavedRecord() {
        return randomUnsavedLoot();
    }

    @Override
    protected SavedLoot whenASavedRecordIsModified(SavedLoot original) {
        return ImmutableSavedLoot.copyOf(original)
                .withPlayerId(new PlayerId(randomString(10)));
    }

    @Override
    protected void assertIdentical(UnsavedLoot original, SavedLoot saved) {
        assertThat(ImmutableUnsavedLoot.builder().from(saved).build(), equalTo(original));
    }

    @Override
    protected void assertIdentical(SavedLoot original, SavedLoot saved) {
        assertThat(original, equalTo(saved));
    }

    @Override
    protected void assertNotIdentical(SavedLoot left, SavedLoot right) {
        assertThat(left, not(equalTo(right)));
    }
}
