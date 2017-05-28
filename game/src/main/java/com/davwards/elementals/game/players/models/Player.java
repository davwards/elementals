package com.davwards.elementals.game.players.models;

public interface Player {
    String name();

    Integer experience();

    Integer health();

    default boolean isAlive() {
        return this.health() > 0;
    }
}
