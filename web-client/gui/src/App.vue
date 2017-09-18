<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
import { PlayerService, TaskService } from 'remote-game-client'
import CredentialsStore from './credentials-store'
import CurrentPlayerInfo from './player/current-player-info'

const playerService = new PlayerService('http://localhost:8080')
const taskService = new TaskService('http://localhost:8080')
const credentialsStore = new CredentialsStore()
const currentPlayerInfo = new CurrentPlayerInfo(playerService, taskService, credentialsStore)

export default {
  name: 'app',

  mounted: function () {
    currentPlayerInfo.startPolling(2000)
    window.onblur = () => { console.log('window lost focus, pause polling'); currentPlayerInfo.stopPolling }
    window.onfocus = () => { console.log('resume polling'); currentPlayerInfo.startPolling(2000) }
  },

  provide: {
    playerService,
    taskService,
    currentPlayerInfo,
    credentialsStore
  }
}
</script>
