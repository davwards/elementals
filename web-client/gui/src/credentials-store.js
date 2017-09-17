export default function () {
  let subscribers = []

  this.subscribe = callback => {
    subscribers.push(callback)
    callback({ id: localStorage.getItem('playerId') })
  }

  this.setCredentials = credentials => {
    localStorage.setItem('playerId', credentials.id)
    subscribers.forEach(callback => callback({ id: credentials.id }))
  }

  this.clearCredentials = () => {
    localStorage.removeItem('playerId')
    subscribers.forEach(callback => callback({ id: null }))
  }
}
