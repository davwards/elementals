package com.davwards.elementals.api.players;

import com.davwards.elementals.game.players.models.SavedPlayer;
import com.fasterxml.jackson.annotation.JsonProperty;

class PlayerResponse {
    @JsonProperty
    private final String name;

    @JsonProperty
    private final String id;

    @JsonProperty
    private final Integer health;

    @JsonProperty
    private final Integer experience;

    @JsonProperty
    private final Integer coin;

    PlayerResponse(SavedPlayer player) {
        this.id = player.getId().toString();
        this.name = player.name();
        this.health = player.health();
        this.experience = player.experience();
        this.coin = player.coin();
    }

}
