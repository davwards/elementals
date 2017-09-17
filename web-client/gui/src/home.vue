<template>
  <player-dashboard v-if="loggedIn"/>
  <signup v-else/>
</template>

<script>
import playerDashboard from './player/player-dashboard'
import signup from './signup/signup-dialogue'

export default {
  name: 'home',

  inject: ['credentialsStore'],

  components: {
    'player-dashboard': playerDashboard,
    'signup': signup
  },

  mounted: function () {
    this.credentialsStore.subscribe(this.updateCredentials.bind(this))
  },

  data: function () {
    return {
      loggedIn: false
    }
  },

  methods: {
    updateCredentials: function (credentials) {
      this.loggedIn = !!credentials.id
    }
  }
}
</script>
