<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
import { PlayerService, TaskService } from 'remote-game-client'
import CredentialsStore from './credentials-store'

export default {
  name: 'app',
  provide: {
    playerService: new PlayerService('http://localhost:8080'),
    taskService: new TaskService('http://localhost:8080'),
    credentialsStore: new CredentialsStore()
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
