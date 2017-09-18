<template>
  <form @submit.prevent="createTask">
    <h4>Create a task</h4>
    <label>Title: <input type="text" v-model="newTaskTitle" name="title"/></label>
    <label>Deadline: <input type="text" v-model="newTaskDeadline" name="deadline"/></label>
    <input type="submit" value="Create task"/>
  </form>
</template>

<script>
export default {
  inject: ['taskService', 'credentialsStore', 'currentPlayerInfo'],

  data: function () {
    return {
      newTaskTitle: '',
      newTaskDeadline: ''
    }
  },

  methods: {
    playerId: function () {
      return this.credentialsStore.getCredentials().id
    },

    createTask: function () {
      this.taskService.createTask(this.playerId(), this.newTaskTitle, this.newTaskDeadline)
        .then(this.currentPlayerInfo.refresh)
    }
  }

}
</script>
