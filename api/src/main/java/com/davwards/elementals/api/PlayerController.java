package com.davwards.elementals.api;

import com.davwards.elementals.game.CreatePlayer;
import com.davwards.elementals.game.FetchPlayer;
import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class PlayerController {

    private final CreatePlayer createPlayer;
    private final FetchPlayer fetchPlayer;

    @Autowired
    public PlayerController(CreatePlayer createPlayer,
                            FetchPlayer fetchPlayer) {
        this.createPlayer = createPlayer;
        this.fetchPlayer = fetchPlayer;
    }

    @RequestMapping(value = "/api/players", method = RequestMethod.POST)
    public ResponseEntity createPlayer(UriComponentsBuilder uriBuilder,
                                       @RequestBody CreatePlayerRequest createPlayerRequest) {

        SavedPlayer player = createPlayer.perform(createPlayerRequest.getName());

        return ResponseEntity.created(
                uriBuilder.path("/api/players/" + player.getId()).build().toUri()
        ).body(new PlayerResponseEnvelope(
                new PlayerResponse(
                        player.getId().toString(),
                        player.getName()
                )
        ));
    }

    @RequestMapping(value = "/api/players/{id}", method = RequestMethod.GET)
    public ResponseEntity fetchPlayer(UriComponentsBuilder uriBuilder,
                                      @PathVariable("id") String id) {

        SavedPlayer player = fetchPlayer.perform(new PlayerId(id));

        return ResponseEntity.ok(new PlayerResponseEnvelope(
                new PlayerResponse(
                        player.getId().toString(),
                        player.getName()
                )
        ));
    }

    private static class PlayerResponseEnvelope {
        @JsonProperty
        private PlayerResponse player;

        PlayerResponseEnvelope(PlayerResponse playerResponse) {
            this.player = playerResponse;
        }
    }

    private static class PlayerResponse {
        @JsonProperty
        private String name;

        @JsonProperty
        private String id;

        PlayerResponse(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class CreatePlayerRequest {
        private String name;

        public String getName() {
            return name;
        }
    }
}
