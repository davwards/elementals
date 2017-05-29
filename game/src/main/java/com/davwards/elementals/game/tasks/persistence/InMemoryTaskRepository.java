package com.davwards.elementals.game.tasks.persistence;

import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.tasks.models.ImmutableSavedTask;
import com.davwards.elementals.game.tasks.models.SavedTask;
import com.davwards.elementals.game.tasks.models.TaskId;
import com.davwards.elementals.game.tasks.models.UnsavedTask;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskRepository implements TaskRepository {
    private Map<TaskId, SavedTask> contents = new HashMap<>();

    @Override
    public List<SavedTask> all() {
        return contents.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public SavedTask save(UnsavedTask unsavedTask) {
        TaskId id = new TaskId(UUID.randomUUID().toString());
        SavedTask savedTask = ImmutableSavedTask.builder()
                .id(id)
                .playerId(unsavedTask.playerId())
                .title(unsavedTask.title())
                .status(unsavedTask.status())
                .deadline(unsavedTask.deadline())
                .build();

        contents.put(id, savedTask);
        return savedTask;
    }

    @Override
    public Optional<SavedTask> find(TaskId id) {
        return Optional.ofNullable(contents.get(id));
    }

    @Override
    public SavedTask update(SavedTask savedTask) {
        contents.put(savedTask.getId(), savedTask);
        return savedTask;
    }

    @Override
    public List<SavedTask> findByPlayerId(PlayerId playerId) {
        return contents.values().stream()
                .filter(task -> task.playerId().equals(playerId))
                .collect(Collectors.toList());
    }
}
