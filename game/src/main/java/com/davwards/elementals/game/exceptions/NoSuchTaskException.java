package com.davwards.elementals.game.exceptions;

import com.davwards.elementals.game.entities.tasks.TaskId;

public class NoSuchTaskException extends RuntimeException {
    private TaskId taskId;

    public NoSuchTaskException(TaskId taskId) {
        this.taskId = taskId;
    }

    public TaskId getTaskId() {
        return taskId;
    }
}
