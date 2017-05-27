package com.davwards.elementals.game.entities.players;

public interface Player {
    String name();

    Integer experience();

    Integer health();

    default public boolean isAlive() {
        return this.health() > 0;
    }
}
