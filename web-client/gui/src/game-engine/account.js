export default function Account (credentialsStore) {
  const playerId = () => credentialsStore.getCredentials().id
  const loggedIn = () => !!playerId()

  const loginSubscribers = []
  const logoutSubscribers = []

  this.playerId = playerId

  this.loggedIn = loggedIn

  this.logout = () => {
    credentialsStore.clearCredentials()
    logoutSubscribers.forEach(callback => callback())
  }

  this.login = playerId => {
    credentialsStore.setCredentials({id: playerId})
    loginSubscribers.forEach(callback => callback(playerId))
  }

  this.subscribe = callbacks => {
    loginSubscribers.push(callbacks.login)
    logoutSubscribers.push(callbacks.logout)
    loggedIn() ? callbacks.login() : callbacks.logout()
  }
}
