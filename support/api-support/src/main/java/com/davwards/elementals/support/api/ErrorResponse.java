package com.davwards.elementals.support.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    @JsonProperty
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
