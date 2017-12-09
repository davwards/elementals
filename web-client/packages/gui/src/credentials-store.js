export default function CredentialsStore () {
  this.setCredentials = credentials => localStorage.setItem('playerId', credentials.id)

  this.getCredentials = () => ({ id: localStorage.getItem('playerId') })

  this.clearCredentials = () => localStorage.removeItem('playerId')
}
