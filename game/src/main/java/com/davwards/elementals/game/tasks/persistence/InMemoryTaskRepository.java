package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.InMemoryRepositoryOfImmutableRecords;
import com.davwards.elementals.game.tasks.models.ImmutableSavedTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.models.UnsavedTask;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskRepository
        extends InMemoryRepositoryOfImmutableRecords<UnsavedTask, SavedTask, TaskId>
        implements TaskRepository {

    @Override
    protected TaskId createId(String value) {
        return new TaskId(value);
    }

    @Override
    protected SavedTask buildSavedRecord(UnsavedTask record, TaskId id) {
        return ImmutableSavedTask.builder().from(record).id(id).build();
    }

    @Override
    public List<SavedTask> findByPlayerId(PlayerId playerId) {
        return contents.values().stream()
                .filter(task -> task.playerId().equals(playerId))
                .collect(Collectors.toList());
    }
}
