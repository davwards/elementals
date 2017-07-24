package com.davwards.elementals.api.habits;

import com.davwards.elementals.habits.FetchHabit;
import com.davwards.elementals.habits.models.HabitId;
import com.davwards.elementals.habits.models.SavedHabit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetHabitEndpoint {

    private static class PossibleResponses implements FetchHabit.Outcome<ResponseEntity> {
        @Override
        public ResponseEntity successfullyFetchedHabit(SavedHabit habit) {
            return ResponseEntity.ok(new HabitResponse(habit));
        }

        @Override
        public ResponseEntity noSuchHabit() {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "api/habits/{id}", method = RequestMethod.GET)
    public ResponseEntity getHabit(@PathVariable("id") String habitId) {
        return fetchHabit.perform(
                new HabitId(habitId),
                new PossibleResponses()
        );
    }

    private final FetchHabit fetchHabit;

    public GetHabitEndpoint(FetchHabit fetchHabit) {
        this.fetchHabit = fetchHabit;
    }
}
