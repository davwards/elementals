package com.davwards.elementals.api;

import com.davwards.elementals.game.players.CreatePlayer;
import com.davwards.elementals.game.players.FetchPlayer;
import com.davwards.elementals.game.players.SavedPlayer;
import com.davwards.elementals.game.players.PlayerId;
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

        return createPlayer.perform(
                createPlayerRequest.getName(),
                player -> ResponseEntity
                        .created(uriBuilder
                                .path("/api/players/" + player.getId())
                                .build()
                                .toUri())
                        .body(playerResponseFor(player)));
    }

    @RequestMapping(value = "/api/players/{id}", method = RequestMethod.GET)
    public ResponseEntity fetchPlayer(UriComponentsBuilder uriBuilder,
                                      @PathVariable("id") String id) {

        return fetchPlayer.perform(
                new PlayerId(id),
                new FetchPlayer.Outcome<ResponseEntity>() {
                    @Override
                    public ResponseEntity foundPlayer(SavedPlayer player) {
                        return ResponseEntity.ok(playerResponseFor(player));
                    }

                    @Override
                    public ResponseEntity noSuchPlayer() {
                        return ResponseEntity.notFound().build();
                    }
                });
    }

    private PlayerResponse playerResponseFor(SavedPlayer player) {
        return new PlayerResponse(
                player.getId().toString(),
                player.name(),
                player.health(),
                player.experience()
        );
    }

    private static class PlayerResponse {
        @JsonProperty
        private final String name;

        @JsonProperty
        private final String id;

        @JsonProperty
        private final Integer health;

        @JsonProperty
        private final Integer experience;

        PlayerResponse(String id, String name, Integer health, Integer experience) {
            this.id = id;
            this.name = name;
            this.health = health;
            this.experience = experience;
        }

        private static class Envelope {
            @JsonProperty
            private PlayerResponse player;

            Envelope(PlayerResponse playerResponse) {
                this.player = playerResponse;
            }
        }
    }

    private static class CreatePlayerRequest {
        private String name;

        public String getName() {
            return name;
        }
    }
}
