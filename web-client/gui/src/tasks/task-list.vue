<template>
  <section>
    <h3>Your tasks</h3>
    <ol v-if="tasks.length > 0">
      <li v-for="task in tasks" :key="task.id">
        <b>{{task.title}}</b> <span v-if="task.deadline">Due: {{task.deadline}}</span> <button v-if="task.status !== 'complete'" @click="completeTask(task)">Done</button>
      </li>
    </ol>
    <p v-else>(no tasks yet)</p>

    <NewTaskForm/>
  </section>
</template>

<script>
import NewTaskForm from './new-task-form'

export default {
  inject: ['taskService', 'currentPlayerInfo'],

  components: { NewTaskForm },

  mounted: function () {
    this.currentPlayerInfo.subscribe({
      newPlayerInfo: info => {
        this.playerId = info.player.id
        this.tasks = info.tasks
      },
      noPlayerInfo: () => {
        this.playerId = null
        this.tasks = []
      }
    })
  },

  data: function () {
    return {
      playerId: null,
      tasks: [],
      newTaskTitle: '',
      newTaskDeadline: ''
    }
  },

  methods: {
    completeTask: function (task) {
      this.taskService.completeTask(task.id)
        .then(this.currentPlayerInfo.refresh)
    }
  }
}
</script>
