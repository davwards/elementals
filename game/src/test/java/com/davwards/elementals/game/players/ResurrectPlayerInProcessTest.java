package com.davwards.elementals.game.players;

import com.davwards.elementals.game.notification.Notification;
import com.davwards.elementals.game.notification.fakes.FakeNotifier;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.models.UnsavedPlayer;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class ResurrectPlayerInProcessTest extends ResurrectPlayerContract {
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private FakeNotifier fakeNotifier = new FakeNotifier();
    private ResurrectPlayer resurrectPlayer = new ResurrectPlayer(playerRepository, fakeNotifier);
    private FetchPlayer fetchPlayer = new FetchPlayer(playerRepository);

    @Override
    ResurrectPlayer useCase() {
        return resurrectPlayer;
    }

    @Override
    FetchPlayer fetchPlayer() {
        return fetchPlayer;
    }

    @Override
    SavedPlayer givenAnExistingPlayer(UnsavedPlayer player) {
        return playerRepository.save(player);
    }

    @Override
    void assertThatNotificationWasSent(PlayerId playerId, Notification.NotificationType type) {
        assertThat(fakeNotifier.notificationsSent(), hasItem(new Notification(playerId, type)));
    }
}
