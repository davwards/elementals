package com.davwards.elementals.game.entities.players;

public class UnsavedPlayer extends Player {
    public UnsavedPlayer(String name) {
        super(name);
    }

    public UnsavedPlayer(String name, Integer experience, Integer health) {
        super(name, experience, health);
    }
}
