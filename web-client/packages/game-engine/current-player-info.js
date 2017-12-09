export default function CurrentPlayerInfo (playerService, taskService, account) {
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
    latestPlayer = null
    latestTasks = null
    noPlayerInfoSubscribers.forEach(callback => callback())
  }

  function update () {
    if (account.loggedIn()) {
      playerService
        .getPlayer(account.playerId())
        .then(player => {
          latestPlayer = player
          reportPlayerInfo()
        }, reportNoPlayerInfo)

      taskService
        .getTasks(account.playerId())
        .then(tasks => {
          latestTasks = tasks
          reportPlayerInfo()
        }, reportNoPlayerInfo)
    }
  }

  account.subscribe({
    login: update,
    logout: reportNoPlayerInfo
  })

  this.refresh = update

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
