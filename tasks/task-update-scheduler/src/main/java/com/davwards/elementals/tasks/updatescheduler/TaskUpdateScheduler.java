package com.davwards.elementals.tasks.updatescheduler;

import com.davwards.elementals.support.scheduling.TimeProvider;
import com.davwards.elementals.tasks.UpdateTaskStatus;
import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.persistence.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class TaskUpdateScheduler {

    private final TaskRepository taskRepository;
    private final UpdateTaskStatus updateTaskStatus;
    private static final Logger logger = LoggerFactory.getLogger(TaskUpdateScheduler.class);
    private final TimeProvider timeProvider;

    public TaskUpdateScheduler(TaskRepository taskRepository, UpdateTaskStatus updateTaskStatus, TimeProvider timeProvider) {
        this.taskRepository = taskRepository;
        this.updateTaskStatus = updateTaskStatus;
        this.timeProvider = timeProvider;
    }

    @Scheduled(fixedDelay = 1000)
    public void updateTasks() {
        taskRepository.all().forEach(task -> updateTaskStatus.perform(
                task.getId(),
                timeProvider.currentTime(),
                new UpdateTaskStatus.Outcome<Void>() {
                    @Override
                    public Void noSuchTask() {
                        logger.error("Tried to check task #" + task.getId() + " for expiration, but it did not exist");
                        return null;
                    }

                    @Override
                    public Void taskExpired(SavedTask updatedTask) {
                        logger.info("Expired task #" + updatedTask.getId() + " at " + timeProvider.currentTime());
                        return null;
                    }

                    @Override
                    public Void noStatusChange(SavedTask task) {
                        logger.error("Checked task #" + task.getId() + " for expiration at " + timeProvider.currentTime() + " but it required no change");
                        return null;
                    }
                }
        ));
    }
}
