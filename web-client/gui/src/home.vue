<template>
  <player-dashboard v-if="loggedIn"/>
  <signup v-else/>
</template>

<script>
import playerDashboard from './player/player-dashboard'
import signup from './signup/signup-dialogue'

export default {
  name: 'home',

  inject: ['currentPlayerInfo', 'credentialsStore'],

  components: {
    'player-dashboard': playerDashboard,
    'signup': signup
  },

  mounted: function () {
    this.currentPlayerInfo.subscribe({
      newPlayerInfo: () => { this.loggedIn = true },
      noPlayerInfo: this.checkWhetherLoggedIn
    })
  },

  data: function () {
    return {
      loggedIn: !!this.credentialsStore.getCredentials().id
    }
  },

  methods: {
    checkWhetherLoggedIn: function () {
      this.loggedIn = !!this.credentialsStore.getCredentials().id
    }
  }
}
</script>
