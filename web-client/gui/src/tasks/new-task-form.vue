<template>
  <form @submit.prevent="createTask">
    <h4>Create a task</h4>
    <p>
      <label>Title: <input type="text" v-model="newTaskTitle" name="title"/></label>
      <label><input type="radio" v-model="taskType" value="standard"/> No Deadline</label>
      <label><input type="radio" v-model="taskType" value="deadline"/> With Deadline</label>
      <label><input type="radio" v-model="taskType" value="recurring"/> Recurring</label>
      <input type="submit" value="Create task"/>
    </p>
    <div>
      <p v-if="taskType === 'deadline'">
        <label>Due on <input type="date" v-model="newTaskDeadlineDate" name="deadline"/></label> <label>at <input type="time" v-model="newTaskDeadlineTime" name="deadline_time"/></label>
      </p>
      <p v-if="taskType === 'recurring'">
        <label>Cadence: <input type="text" v-model="newTaskCadence" name="deadline"/></label>
        <label>Duration: <input type="text" v-model="newTaskDuration" name="deadline"/></label>
      </p>
    </div>
  </form>
</template>

<script>
const taskCreators = {
  standard: (tasks, vm) => { tasks.create(vm.newTaskTitle, '') },
  deadline: (tasks, vm) => { tasks.create(vm.newTaskTitle, vm.newTaskDeadline) },
  recurring: (tasks, vm) => {
    tasks.createRecurring(
      vm.newTaskTitle,
      vm.cadenceAndDuration.cadence,
      vm.cadenceAndDuration.duration
    )
  }
}

export default {
  inject: ['gameEngine'],

  data: function () {
    return {
      newTaskTitle: '',
      newTaskDeadlineDate: '',
      newTaskDeadlineTime: '23:59',
      taskType: 'standard'
    }
  },

  computed: {
    newTaskDeadline: function () {
      if (!this.newTaskDeadlineDate || !this.newTaskDeadlineTime) return ''
      return `${this.newTaskDeadlineDate}T${this.newTaskDeadlineTime}`
    }
  },

  methods: {
    createTask: function () { taskCreators[this.taskType](this.gameEngine.tasks, this) }
  }

}
</script>
