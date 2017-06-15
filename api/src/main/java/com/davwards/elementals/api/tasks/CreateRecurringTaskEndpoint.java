package com.davwards.elementals.api.tasks;

import com.davwards.elementals.api.support.responses.ErrorResponse;
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

import static com.davwards.elementals.game.support.language.Either.failure;
import static com.davwards.elementals.game.support.language.Either.success;

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

        ResponseEntity malformedDuration(DateTimeParseException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getMessage()));
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

        PossibleResponses possibleResponses = new PossibleResponses(uriComponentsBuilder);

        return parseDuration(request.duration)
                .map(duration -> createRecurringTask
                        .perform(
                                request.title,
                                new PlayerId(playerId),
                                request.cadence,
                                duration,
                                possibleResponses
                        ))
                .orIfFailure(possibleResponses::malformedDuration);
    }

    private Either<Period, DateTimeParseException> parseDuration(String duration) {
        try {
            return success(Period.parse(duration));
        } catch (DateTimeParseException e) {
            return failure(e);
        }
    }

    private final CreateRecurringTask createRecurringTask;

    public CreateRecurringTaskEndpoint(CreateRecurringTask createRecurringTask) {
        this.createRecurringTask = createRecurringTask;
    }
}
