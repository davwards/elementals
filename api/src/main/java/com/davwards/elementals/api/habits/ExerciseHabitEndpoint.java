package com.davwards.elementals.api.habits;

import com.davwards.elementals.api.players.PlayerResponse;
import com.davwards.elementals.api.support.responses.ErrorResponse;
import com.davwards.elementals.game.habits.ExerciseHabit;
import com.davwards.elementals.game.habits.models.HabitId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.support.language.Either;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.davwards.elementals.game.habits.ExerciseHabit.Sides.DOWNSIDE;
import static com.davwards.elementals.game.habits.ExerciseHabit.Sides.UPSIDE;
import static com.davwards.elementals.support.language.Either.failure;
import static com.davwards.elementals.support.language.Either.success;

@RestController
public class ExerciseHabitEndpoint {

    private static class ExerciseHabitRequest {
        @JsonProperty
        private String side;
    }

    private static class PossibleResponses implements ExerciseHabit.Outcome<ResponseEntity> {

        @Override
        public ResponseEntity successfullyExercisedHabit(SavedPlayer player) {
            return ResponseEntity
                    .ok(new PlayerResponse(player));
        }

        @Override
        public ResponseEntity noSuchHabit() {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        @Override
        public ResponseEntity habitDoesNotHaveSpecifiedSide() {
            return ResponseEntity
                    .unprocessableEntity()
                    .body(new ErrorResponse("Habit does not have the specified side"));
        }

        ResponseEntity invalidSide(String error) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(error));
        }
    }

    @RequestMapping(
            value = "api/habits/{id}/exercise",
            method = RequestMethod.PUT)
    public ResponseEntity exerciseHabit(
            @PathVariable("id") String habitId,
            @RequestBody ExerciseHabitRequest request) {

        PossibleResponses responses = new PossibleResponses();

        return parseSide(request.side)
                .map(side -> exerciseHabit.perform(
                        new HabitId(habitId),
                        side,
                        responses))
                .orIfFailure(responses::invalidSide);
    }

    private static Either<ExerciseHabit.Sides, String> parseSide(String side) {
        switch (side) {
            case "up":
                return success(UPSIDE);
            case "down":
                return success(DOWNSIDE);
            default:
                return failure("Invalid side; correct values are \"up\" or \"down\".");
        }
    }

    private final ExerciseHabit exerciseHabit;

    public ExerciseHabitEndpoint(ExerciseHabit exerciseHabit) {
        this.exerciseHabit = exerciseHabit;
    }
}
