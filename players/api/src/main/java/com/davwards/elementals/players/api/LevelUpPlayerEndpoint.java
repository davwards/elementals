package com.davwards.elementals.players.api;

import com.davwards.elementals.players.LevelUpPlayer;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.support.api.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LevelUpPlayerEndpoint {

    private class PossibleResponses implements LevelUpPlayer.Outcome<ResponseEntity> {
        @Override
        public ResponseEntity successfullyUpdatedPlayer(SavedPlayer updatedPlayer) {
            return ResponseEntity
                    .ok(new PlayerResponse(updatedPlayer));
        }

        @Override
        public ResponseEntity playerCannotLevel() {
            return ResponseEntity
                    .unprocessableEntity()
                    .body(new ErrorResponse("Player " + playerId + " can't level up yet"));
        }

        @Override
        public ResponseEntity noSuchPlayer() {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        private final String playerId;

        private PossibleResponses(String playerId) {
            this.playerId = playerId;
        }
    }

    @RequestMapping(method= RequestMethod.PATCH, value="/api/players/{id}/level-up")
    public ResponseEntity perform(@PathVariable("id") String playerId) {
        return levelUpPlayer.perform(new PlayerId(playerId), new PossibleResponses(playerId));
    }

    private final LevelUpPlayer levelUpPlayer;

    public LevelUpPlayerEndpoint(LevelUpPlayer levelUpPlayer) {
        this.levelUpPlayer = levelUpPlayer;
    }
}
