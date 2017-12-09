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
      <RecurringTaskSubform v-if="taskType === 'recurring'" v-model="cadenceAndDuration"/>
    </div>
  </form>
</template>

<script>
import RecurringTaskSubform from './recurring-task-subform'

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

  components: { RecurringTaskSubform },

  data: function () {
    return {
      newTaskTitle: '',
      newTaskDeadlineDate: '',
      newTaskDeadlineTime: '23:59',
      taskType: 'standard',
      cadenceAndDuration: {
        cadence: '',
        duration: ''
      }
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
  },

  watch: {
    cadenceAndDuration: function (newValue) {
      console.log('cad changed:')
      console.log(newValue.cadence)
      console.log(newValue.duration)
    }
  }

}
</script>
