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
currentPlayerInfo.startPolling(10000)

export default {
  name: 'app',
  provide: {
    playerService,
    taskService,
    currentPlayerInfo,
    credentialsStore
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
