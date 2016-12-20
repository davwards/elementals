package com.davwards.elementals.game.entities.players;

import com.davwards.elementals.game.GameConstants;

public class Player {
    protected Integer experience = 0;
    protected String name;
    private Integer health = GameConstants.STARTING_HEALTH;

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, Integer experience, Integer health) {
        this.name = name;
        this.experience = experience;
        this.health = health;
    }

    public Integer getExperience() {
        return experience;
    }

    public String getName() {
        return name;
    }

    public void addExperience(Integer points) {
        this.experience += points;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHealth() {
        return health;
    }

    public void decreaseHealth(Integer points) {
        this.health -= points;
    }
}
