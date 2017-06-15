package com.davwards.elementals.game.tasks.recurring;

import com.davwards.elementals.game.support.language.StrictOptional;
import com.davwards.elementals.game.support.persistence.SavedEntity;
import com.davwards.elementals.game.tasks.models.*;
import com.davwards.elementals.game.tasks.persistence.RecurringTaskRepository;
import com.davwards.elementals.game.tasks.persistence.TaskRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

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
                .map(recurringTask -> taskShouldSpawn(recurringTask, currentTime)
                        ? handle.spawnedNewTask(createTask(recurringTask, currentTime))
                        : handle.taskNotDueToSpawn())
                .orElseGet(handle::noSuchRecurringTask);
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

    private Boolean taskShouldSpawn(SavedRecurringTask recurringTask, LocalDateTime currentTime) {
        return latestScheduledOccurrence(recurringTask, currentTime)
                .map(isLaterThanLatestInstanceOfTask(recurringTask))
                .orElse(false);
    }

    private Function<LocalDateTime, Boolean> isLaterThanLatestInstanceOfTask(SavedRecurringTask recurringTask) {
        return time -> taskRepository
                .findByParentRecurringTaskId(recurringTask.getId())
                .stream()
                .map(SavedEntity::createdAt)
                .max(Comparator.naturalOrder())
                .map(lastTimeSpawned -> lastTimeSpawned.isBefore(time))
                .orElse(false);
    }

    private Optional<LocalDateTime> latestScheduledOccurrence(SavedRecurringTask recurringTask, LocalDateTime currentTime) {
        Iterator<LocalDateTime> scheduledOccurrences =
                cadenceInterpreter.nextOccurrences(
                        recurringTask.createdAt(),
                        recurringTask.cadence()
                );

        Optional<LocalDateTime> result = Optional.empty();
        while (scheduledOccurrences.hasNext()) {
            LocalDateTime next = scheduledOccurrences.next();
            if (next.isAfter(currentTime)) break;
            result = Optional.of(next);
        }
        return result;
    }

    private SavedTask createTask(SavedRecurringTask recurringTask, LocalDateTime currentTime) {
        return taskRepository.save(ImmutableUnsavedTask.builder()
                .title(recurringTask.title())
                .playerId(recurringTask.playerId())
                .status(Task.Status.INCOMPLETE)
                .deadline(currentTime.plus(recurringTask.duration()))
                .parentRecurringTaskId(recurringTask.getId())
                .build()
        );
    }
}
