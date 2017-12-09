<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
import { PlayerService, TaskService } from 'remote-game-client'
import CredentialsStore from './credentials-store'
import GameEngine from './game-engine/game-engine'

const gameEngine = new GameEngine(
  new PlayerService('http://localhost:8080'),
  new TaskService('http://localhost:8080'),
  new CredentialsStore()
)

export default {
  name: 'app',

  mounted: function () {
    gameEngine.player.startPolling(2000)
    window.onblur = () => { console.log('window lost focus, pause polling'); gameEngine.player.stopPolling() }
    window.onfocus = () => { console.log('resume polling'); gameEngine.player.startPolling(2000) }
  },

  provide: { gameEngine }
}
</script>
