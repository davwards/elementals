export default function CurrentPlayerInfo (playerService, taskService, credentialsStore) {
  let latestPlayer = null
  let latestTasks = null
  let timer = null

  const newPlayerInfoSubscribers = []
  const noPlayerInfoSubscribers = []

  function reportPlayerInfo () {
    if (latestPlayer && latestTasks) {
      newPlayerInfoSubscribers.forEach(callback => {
        callback({
          player: latestPlayer,
          tasks: latestTasks
        })
      })
    }
  }

  function reportNoPlayerInfo () {
    credentialsStore.clearCredentials()
    latestPlayer = null
    latestTasks = null
    noPlayerInfoSubscribers.forEach(callback => callback())
  }

  function update () {
    if (credentialsStore.getCredentials().id) {
      playerService
        .getPlayer(credentialsStore.getCredentials().id)
        .then(player => {
          latestPlayer = player
          reportPlayerInfo()
        }, reportNoPlayerInfo)

      taskService
        .getTasks(credentialsStore.getCredentials().id)
        .then(tasks => {
          latestTasks = tasks
          reportPlayerInfo()
        }, reportNoPlayerInfo)
    }
  }

  this.setPlayer = newPlayerId => {
    credentialsStore.setCredentials({id: newPlayerId})
    update()
  }

  this.clearPlayer = () => {
    reportNoPlayerInfo()
  }

  this.refresh = () => { update() }

  this.subscribe = (callbacks) => {
    newPlayerInfoSubscribers.push(callbacks.newPlayerInfo)
    noPlayerInfoSubscribers.push(callbacks.noPlayerInfo)

    if (latestPlayer && latestTasks) {
      callbacks.newPlayerInfo({
        player: latestPlayer,
        tasks: latestTasks
      })
    } else {
      callbacks.noPlayerInfo()
    }
  }

  this.startPolling = (interval = 5000) => {
    update()
    if (!timer) timer = setInterval(update, interval)
  }

  this.stopPolling = () => {
    if (timer) clearInterval(timer)
    timer = null
  }
}
