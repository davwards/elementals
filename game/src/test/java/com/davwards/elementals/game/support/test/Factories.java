package com.davwards.elementals.game.support.test;

import com.davwards.elementals.game.loot.models.KindOfLootId;
import com.davwards.elementals.game.loot.models.ImmutableUnsavedLoot;
import com.davwards.elementals.game.players.models.ImmutableUnsavedPlayer;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedRecurringTask;
import com.davwards.elementals.game.tasks.models.ImmutableUnsavedTask;
import com.davwards.elementals.game.tasks.models.Task;

import java.time.Period;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Factories {
    public static String randomString(int length) {
        return UUID.randomUUID().toString().substring(0, length);
    }

    public static int randomInt(int minInclusive, int maxInclusive) {
        return ThreadLocalRandom.current().nextInt(minInclusive, maxInclusive + 1);
    }

    public static ImmutableUnsavedPlayer randomUnsavedPlayer() {
        return ImmutableUnsavedPlayer.builder()
                .name("Test Player " + randomString(10))
                .experience(randomInt(0, 100))
                .health(randomInt(1,100))
                .coin(randomInt(0, 500))
                .build();
    }

    public static ImmutableUnsavedTask randomUnsavedTask() {
        return ImmutableUnsavedTask.builder()
                .title("Test Task " + randomString(10))
                .playerId(new PlayerId(randomString(10)))
                .status(Task.Status.INCOMPLETE)
                .build();
    }

    public static ImmutableUnsavedRecurringTask randomUnsavedRecurringTask() {
        return ImmutableUnsavedRecurringTask.builder()
                .playerId(new PlayerId("some-player"))
                .title("Test Recurring Task " + randomString(10))
                .cadence("FREQ=DAILY")
                .duration(Period.ofDays(1))
                .build();
    }

    public static ImmutableUnsavedLoot randomUnsavedLoot() {
        return ImmutableUnsavedLoot.builder()
                .playerId(new PlayerId("some-player"))
                .kindId(KindOfLootId.COPPER_SWORD)
                .build();
    }
}
