<template>
  <article>
    <h1>{{welcomeMessage}}</h1>
    <StatsSummary :player="player"/>
    <TaskList/>
    <button @click="signout">Sign out</button>
  </article>
</template>

<script>
import StatsSummary from './stats-summary'
import TaskList from '../tasks/task-list'

export default {
  name: 'player-dashboard',

  components: { StatsSummary, TaskList },

  inject: ['gameEngine'],

  mounted: function () {
    this.gameEngine.player.subscribe({
      newPlayerInfo: info => {
        this.playerId = info.player.id
        this.player = info.player
        this.tasks = info.tasks
      },
      noPlayerInfo: () => {
        this.playerId = null
        this.player = {}
        this.tasks = []
      }
    })
  },

  data: function () {
    return {
      playerId: null,
      player: {},
      tasks: [],
      newTaskTitle: '',
      newTaskDeadline: ''
    }
  },

  computed: {
    welcomeMessage: function () {
      return this.player.name || 'Loading...'
    }
  },

  methods: {
    signout: function () {
      this.gameEngine.account.logout()
    }
  }
}
</script>
