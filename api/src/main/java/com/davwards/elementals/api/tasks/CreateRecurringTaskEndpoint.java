package com.davwards.elementals.api.tasks;

import com.davwards.elementals.api.support.responses.ResourceCreatedResponses;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.language.Either;
import com.davwards.elementals.game.tasks.models.SavedRecurringTask;
import com.davwards.elementals.game.tasks.recurring.CreateRecurringTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Period;
import java.time.format.DateTimeParseException;

@RestController
public class CreateRecurringTaskEndpoint {

    private static class CreateRecurringTaskRequest {
        @JsonProperty
        private String title;

        @JsonProperty
        private String cadence;

        @JsonProperty
        private String duration;
    }

    private static class PossibleResponses extends ResourceCreatedResponses<SavedRecurringTask>
            implements CreateRecurringTask.Outcome<ResponseEntity> {
        @Override
        public ResponseEntity successfullyCreatedRecurringTask(SavedRecurringTask createdTask) {
            return ResponseEntity
                    .created(resourceLocation(createdTask))
                    .body(new RecurringTaskResponse(createdTask));
        }

        private PossibleResponses(UriComponentsBuilder uriBuilder) {
            super(uriBuilder, "api/recurring-tasks");
        }
    }

    @RequestMapping(
            value = "api/players/{playerId}/recurring-tasks",
            method = RequestMethod.POST)
    public ResponseEntity createRecurringTask(
            UriComponentsBuilder uriComponentsBuilder,
            @PathVariable("playerId") String playerId,
            @RequestBody CreateRecurringTaskRequest request) {

        return parseDuration(request.duration).join(
                duration -> createRecurringTask.perform(
                        request.title,
                        new PlayerId(playerId),
                        request.cadence,
                        duration,
                        new PossibleResponses(uriComponentsBuilder)
                ),

                dateTimeParseException ->
                        ResponseEntity.badRequest().body(
                                "{\"error\":\"Bad duration syntax. Duration should follow ISO-8601 format for durations up to 'day' level of granularity, PnYnMnD or PnW.\"}"
                        )
        );
    }

    private Either<Period, DateTimeParseException> parseDuration(String duration) {
        try {
            return new Either.Success<>(Period.parse(duration));
        } catch (DateTimeParseException e) {
            return new Either.Failure<>(e);
        }
    }

    private final CreateRecurringTask createRecurringTask;

    public CreateRecurringTaskEndpoint(CreateRecurringTask createRecurringTask) {
        this.createRecurringTask = createRecurringTask;
    }
}
