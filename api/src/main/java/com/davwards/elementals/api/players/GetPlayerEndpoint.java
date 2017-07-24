package com.davwards.elementals.api.players;

import com.davwards.elementals.players.FetchPlayer;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.players.models.SavedPlayer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class GetPlayerEndpoint {
    private final FetchPlayer fetchPlayer;

    public GetPlayerEndpoint(FetchPlayer fetchPlayer) {
        this.fetchPlayer = fetchPlayer;
    }

    @RequestMapping(value = "/api/players/{id}", method = RequestMethod.GET)
    public ResponseEntity fetchPlayer(UriComponentsBuilder uriBuilder,
                                      @PathVariable("id") String id) {

        return fetchPlayer.perform(
                new PlayerId(id),
                new FetchPlayer.Outcome<ResponseEntity>() {
                    @Override
                    public ResponseEntity foundPlayer(SavedPlayer player) {
                        return ResponseEntity.ok(new PlayerResponse(player));
                    }

                    @Override
                    public ResponseEntity noSuchPlayer() {
                        return ResponseEntity.notFound().build();
                    }
                });
    }

}
