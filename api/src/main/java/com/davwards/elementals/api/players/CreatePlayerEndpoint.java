package com.davwards.elementals.api.players;

import com.davwards.elementals.api.support.responses.ResourceCreatedResponses;
import com.davwards.elementals.players.CreatePlayer;
import com.davwards.elementals.players.models.SavedPlayer;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class CreatePlayerEndpoint {

    private static class CreatePlayerRequest {
        @JsonProperty
        private String name;
    }

    private static class PossibleResponses extends ResourceCreatedResponses<SavedPlayer>
            implements CreatePlayer.Outcome<ResponseEntity> {

        @Override
        public ResponseEntity playerSaved(SavedPlayer player) {
            return ResponseEntity
                    .created(resourceLocation(player))
                    .body(new PlayerResponse(player));
        }

        private PossibleResponses(UriComponentsBuilder uriBuilder) {
            super(uriBuilder, "api/players");
        }
    }

    @RequestMapping(value = "/api/players", method = RequestMethod.POST)
    public ResponseEntity createPlayer(UriComponentsBuilder uriBuilder,
                                       @RequestBody CreatePlayerRequest createPlayerRequest) {

        return createPlayer.perform(
                createPlayerRequest.name,
                new PossibleResponses(uriBuilder));
    }

    private final CreatePlayer createPlayer;

    public CreatePlayerEndpoint(CreatePlayer createPlayer) {
        this.createPlayer = createPlayer;
    }
}
