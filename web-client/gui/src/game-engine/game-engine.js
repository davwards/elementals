import CurrentPlayerInfo from './current-player-info'
import Account from './account'

export default function GameEngine (playerService, taskService, credentialsStore) {
  const account = new Account(credentialsStore)
  const currentPlayerInfo = new CurrentPlayerInfo(playerService, taskService, account)

  this.account = account

  this.tasks = {
    create: (title, deadline) => taskService
      .createTask(account.playerId(), title, deadline)
      .then(currentPlayerInfo.refresh),

    complete: id => taskService
      .completeTask(id)
      .then(currentPlayerInfo.refresh)
  }

  this.player = {
    create: name => playerService
      .createPlayer({ name })
      .then(player => account.login(player.id)),

    subscribe: currentPlayerInfo.subscribe,

    startPolling: currentPlayerInfo.startPolling,

    stopPolling: currentPlayerInfo.stopPolling
  }
}
