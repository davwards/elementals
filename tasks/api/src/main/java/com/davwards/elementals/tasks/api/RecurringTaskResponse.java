package com.davwards.elementals.tasks.api;

import com.davwards.elementals.tasks.models.SavedRecurringTask;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RecurringTaskResponse {
    @JsonProperty
    private final String id;

    @JsonProperty
    private final String playerId;

    @JsonProperty
    private final String title;

    @JsonProperty
    private final String cadence;

    @JsonProperty
    private final String duration;

    @JsonProperty
    private final String createdAt;

    public RecurringTaskResponse(SavedRecurringTask recurringTask) {
        this.id = recurringTask.getId().toString();
        this.playerId = recurringTask.playerId().toString();
        this.title = recurringTask.title();
        this.cadence = recurringTask.cadence();
        this.duration = recurringTask.duration().toString();
        this.createdAt = recurringTask.createdAt().toString();
    }
}
