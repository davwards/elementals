<template>
  <player-dashboard v-if="loggedIn"/>
  <signup v-else/>
</template>

<script>
import playerDashboard from './player/player-dashboard'
import signup from './signup/signup-dialogue'

export default {
  name: 'home',

  inject: ['gameEngine'],

  components: {
    'player-dashboard': playerDashboard,
    'signup': signup
  },

  mounted: function () {
    this.gameEngine.account.subscribe({
      login: () => { this.loggedIn = true },
      logout: () => { this.loggedIn = false }
    })
  },

  data: function () {
    return {
      loggedIn: this.gameEngine.account.loggedIn()
    }
  }
}
</script>
