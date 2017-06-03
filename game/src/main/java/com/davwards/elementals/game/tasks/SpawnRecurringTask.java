package com.davwards.elementals.game.tasks;

import com.davwards.elementals.game.support.language.StrictOptional;
import com.davwards.elementals.game.support.persistence.SavedEntity;
import com.davwards.elementals.game.tasks.models.*;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.time.LocalDateTime;
import java.util.*;

import static com.davwards.elementals.game.support.language.StrictOptional.strict;

public class SpawnRecurringTask {
    public interface Outcome<T> {
        T spawnedNewTask(SavedTask task);

        T taskNotDueToSpawn();

        T noSuchRecurringTask();
    }

    public <T> T perform(RecurringTaskId recurringTaskId,
                         LocalDateTime currentTime,
                         Outcome<T> handle) {

        return strict(recurringTaskRepository.find(recurringTaskId))
                .map(recurringTask -> latestScheduledOccurrence(recurringTask, currentTime)
                        .map(x -> createTask(recurringTask, currentTime))
                        .map(handle::spawnedNewTask)
                        .orElseGet(handle::taskNotDueToSpawn))
                .orElseGet(handle::noSuchRecurringTask);
    }

    private StrictOptional<LocalDateTime> latestScheduledOccurrence(SavedRecurringTask recurringTask, LocalDateTime currentTime) {
        Iterator<LocalDateTime> scheduledOccurrences =
                cadenceInterpreter.nextOccurrences(
                        recurringTask.createdAt(),
                        recurringTask.cadence()
                );

        StrictOptional<LocalDateTime> result = StrictOptional.empty();
        while (scheduledOccurrences.hasNext()) {
            LocalDateTime next = scheduledOccurrences.next();
            if (!next.isAfter(currentTime)) {
                result = StrictOptional.of(next);
            } else {
                break;
            }
        }

        LocalDateTime lastOccurrence = taskRepository
                .findByParentRecurringTaskId(recurringTask.getId())
                .stream()
                .map(SavedEntity::createdAt)
                .max(Comparator.naturalOrder())
                .orElse(recurringTask.createdAt().minusMinutes(1));

        return result.filter(lastOccurrence::isBefore);
    }

    private SavedTask createTask(SavedRecurringTask recurringTask, LocalDateTime currentTime) {
        return taskRepository.save(
                ImmutableUnsavedTask.builder()
                        .title(recurringTask.title())
                        .playerId(recurringTask.playerId())
                        .status(Task.Status.INCOMPLETE)
                        .deadline(currentTime.plus(recurringTask.duration()))
                        .parentRecurringTaskId(recurringTask.getId())
                        .build());
    }

    private final RecurringTaskRepository recurringTaskRepository;
    private final TaskRepository taskRepository;
    private final CadenceInterpreter cadenceInterpreter;

    public SpawnRecurringTask(RecurringTaskRepository recurringTaskRepository,
                              TaskRepository taskRepository,
                              CadenceInterpreter cadenceInterpreter) {

        this.recurringTaskRepository = recurringTaskRepository;
        this.taskRepository = taskRepository;
        this.cadenceInterpreter = cadenceInterpreter;
    }
}
