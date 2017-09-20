<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
import { PlayerService, TaskService } from 'remote-game-client'
import CredentialsStore from './credentials-store'
import GameEngine from './game-engine/game-engine'

const playerService = new PlayerService('http://localhost:8080')
const taskService = new TaskService('http://localhost:8080')
const credentialsStore = new CredentialsStore()
const gameEngine = new GameEngine(playerService, taskService, credentialsStore)

export default {
  name: 'app',

  mounted: function () {
    gameEngine.player.startPolling(2000)
    window.onblur = () => { console.log('window lost focus, pause polling'); gameEngine.player.stopPolling }
    window.onfocus = () => { console.log('resume polling'); gameEngine.player.startPolling(2000) }
  },

  provide: {
    credentialsStore,
    gameEngine
  }
}
</script>
