package com.davwards.elementals.tasks.api;

import com.davwards.elementals.tasks.models.SavedTask;
import com.davwards.elementals.tasks.models.Task;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.format.DateTimeFormatter;

class TaskResponse {
    @JsonProperty
    private final String title;

    @JsonProperty
    private final String deadline;

    @JsonProperty
    private final String playerId;

    @JsonProperty
    private final String status;

    TaskResponse(SavedTask task) {
        this.title = task.title();
        this.deadline = task.deadline()
                        .map(deadline -> deadline.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .orElse(null);
        this.playerId = task.playerId().toString();
        this.status = statusValue(task.status());
    }

    private String statusValue(Task.Status status) {
        switch (status) {
            case COMPLETE:
                return "complete";
            case INCOMPLETE:
                return "incomplete";
            case PAST_DUE:
                return "pastDue";
            default:
                throw new RuntimeException("Could not translate unknown Status value: " + status);
        }
    }
}
