package com.davwards.elementals.api.habits;

import com.davwards.elementals.api.support.responses.ErrorResponse;
import com.davwards.elementals.api.support.responses.ResourceCreatedResponses;
import com.davwards.elementals.game.habits.CreateHabit;
import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.language.Either;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import static com.davwards.elementals.game.habits.CreateHabit.Sides.BOTH_SIDES;
import static com.davwards.elementals.game.habits.CreateHabit.Sides.DOWNSIDE;
import static com.davwards.elementals.game.habits.CreateHabit.Sides.UPSIDE;
import static com.davwards.elementals.game.support.language.Either.failure;
import static com.davwards.elementals.game.support.language.Either.success;

@RestController
public class CreateHabitEndpoint {

    private static class CreateHabitRequest {
        @JsonProperty
        private String title;
        @JsonProperty
        private String sides;
        @JsonProperty
        private String createdAt;
    }

    private static class PossibleResponses extends ResourceCreatedResponses<SavedHabit>
            implements CreateHabit.Outcome<ResponseEntity> {

        @Override
        public ResponseEntity successfullyCreatedHabit(SavedHabit createdHabit) {
            return ResponseEntity
                    .created(resourceLocation(createdHabit))
                    .body(new HabitResponse(createdHabit));
        }

        static ResponseEntity malformedRequest(String error) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(error));
        }

        PossibleResponses(UriComponentsBuilder uriBuilder) {
            super(uriBuilder, "api/habits");
        }
    }

    @RequestMapping(
            value = "api/players/{playerId}/habits",
            method = RequestMethod.POST)
    public ResponseEntity createHabit(
            UriComponentsBuilder uriBuilder,
            @PathVariable("playerId") String playerId,
            @RequestBody CreateHabitRequest request) {

        return parseSide(request.sides).join(
                side -> createHabit
                        .perform(
                                new PlayerId(playerId),
                                request.title,
                                side,
                                new PossibleResponses(uriBuilder)
                        ),
                PossibleResponses::malformedRequest
        );
    }

    private Either<CreateHabit.Sides, String> parseSide(String side) {
        switch(side) {
            case "up":
                return success(UPSIDE);
            case "down":
                return success(DOWNSIDE);
            case "both":
                return success(BOTH_SIDES);
            default:
                return failure("Invalid side value");
        }
    }

    private final CreateHabit createHabit;

    public CreateHabitEndpoint(CreateHabit createHabit) {
        this.createHabit = createHabit;
    }
}
