import CurrentPlayerInfo from './current-player-info'

export default function GameEngine (playerService, taskService, credentialsStore) {
  const currentPlayerInfo = new CurrentPlayerInfo(playerService, taskService, credentialsStore)

  const playerId = () => credentialsStore.getCredentials().id
  const loggedIn = () => !!playerId()

  const loginSubscribers = []
  const logoutSubscribers = []

  this.account = {
    logout: () => {
      currentPlayerInfo.clearPlayer()
      logoutSubscribers.forEach(callback => callback())
    },

    loggedIn: loggedIn,

    subscribe: callbacks => {
      loginSubscribers.push(callbacks.login)
      logoutSubscribers.push(callbacks.logout)
      loggedIn() ? callbacks.login() : callbacks.logout()
    }
  }

  this.tasks = {
    create: (title, deadline) => taskService
      .createTask(playerId(), title, deadline)
      .then(currentPlayerInfo.refresh),

    complete: id => taskService
      .completeTask(id)
      .then(currentPlayerInfo.refresh)
  }

  this.player = {
    create: name => playerService
      .createPlayer({ name })
      .then(player => currentPlayerInfo.setPlayer(player.id))
      .then(() => loginSubscribers.forEach(callback => callback())),

    subscribe: currentPlayerInfo.subscribe,

    startPolling: currentPlayerInfo.startPolling,

    stopPolling: currentPlayerInfo.stopPolling
  }
}
