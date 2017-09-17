<template>
  <article>
    <h1>Welcome, {{player.name}}!</h1>
    <h3>Your stats:</h3>
    <dl>
      <dt>Health:</dt>
      <dd>{{player.health}}</dd>

      <dt>Experience:</dt>
      <dd>{{player.experience}}</dd>

      <dt>Coin:</dt>
      <dd>{{player.coin}}</dd>
    </dl>

    <h3>Your tasks:</h3>
    <ol v-if="tasks.length > 0">
      <li v-for="task in tasks">
        <b>{{task.title}}</b> <span v-if="task.deadline">Due: {{task.deadline}}</span> <button v-if="task.status !== 'complete'" @click="completeTask(task)">Done</button>
      </li>
    </ol>
    <p v-else>(no tasks yet)</p>

    <h4>Create a task:</h4>
    <form @submit.prevent="createTask">
      <label>Title: <input type="text" v-model="newTaskTitle" name="title"/></label>
      <label>Deadline: <input type="text" v-model="newTaskDeadline" name="deadline"/></label>
      <input type="submit" value="Create task"/>
    </form>

    <button @click="signout">Sign out</button>
  </article>
</template>

<script>
export default {
  name: 'player-dashboard',

  inject: ['playerService', 'taskService', 'credentialsStore'],

  mounted: function () {
    this.credentialsStore.subscribe(credentials => {
      this.playerId = credentials.id
      if (this.playerId) {
        this.updatePlayer()
        this.updateTasks()
      } else {
        this.player = {}
        this.tasks = []
      }
    })

    setInterval(() => {
      if (this.playerId) {
        this.updatePlayer()
      }
    }, 5000)
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

  methods: {
    createTask: function () {
      this.taskService.createTask(this.playerId, this.newTaskTitle, this.newTaskDeadline)
        .then(this.updateTasks)
    },

    completeTask: function (task) {
      console.log('completing task')
      console.log(task)
      this.taskService.completeTask(task.id)
        .then(task => {
          this.updateTasks()
          this.updatePlayer()
        })
    },

    updateTasks: function () {
      this.taskService.getTasks(this.playerId)
        .then(tasks => { this.tasks = tasks })
    },

    updatePlayer: function () {
      this.playerService.getPlayer(this.playerId)
        .then(player => { this.player = player })
    },

    signout: function () {
      this.credentialsStore.clearCredentials()
    }
  }
}
</script>
