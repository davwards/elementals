<template>
  <div>
    <ul>
      <li>
        <label>
          <input type="radio"
                 name="recurring_task_schedule_type"
                 v-model="schedule"
                 value="daily"
          /> Every day
        </label>
      </li>
      <li>
        <label>
          <input type="radio" name="recurring_task_schedule_type" v-model="schedule" value="everyXDays"/> Every X days
        </label>
        <label> X: <input type="text" v-model="everyXDays" /></label>
      </li>
      <li>
        <label>
          <input type="radio" name="recurring_task_schedule_type" v-model="schedule" value="onDaysInWeek"/> On certain days of the week
        </label>
        <label>
          <input type="checkbox" name="recurring_task_schedule_days_in_week" v-model="onDaysInWeek" value="MO"/> Monday
        </label>
        <label>
          <input type="checkbox" name="recurring_task_schedule_days_in_week" v-model="onDaysInWeek" value="TU"/> Tuesday
        </label>
        <label>
          <input type="checkbox" name="recurring_task_schedule_days_in_week" v-model="onDaysInWeek" value="WE"/> Wednesday
        </label>
        <label>
          <input type="checkbox" name="recurring_task_schedule_days_in_week" v-model="onDaysInWeek" value="TH"/> Thursday
        </label>
        <label>
          <input type="checkbox" name="recurring_task_schedule_days_in_week" v-model="onDaysInWeek" value="FR"/> Friday
        </label>
        <label>
          <input type="checkbox" name="recurring_task_schedule_days_in_week" v-model="onDaysInWeek" value="SA"/> Saturday
        </label>
        <label>
          <input type="checkbox" name="recurring_task_schedule_days_in_week" v-model="onDaysInWeek" value="SU"/> Sunday
        </label>
      </li>
    </ul>
  </div>
</template>

<script>
const interpretCadenceAndDuration = (cadence, duration) => {
  const dailyPattern = /^FREQ:DAILY$/
  const everyXDaysPattern = /^FREQ:DAILY;INTERVAL=(\d+)$/
  const onDaysInWeekPattern = /^FREQ:DAILY;BYDAY=([SUMOTWEHFRA,]+)$/

  if (dailyPattern.test(cadence)) {
    return {
      schedule: 'daily'
    }
  }

  if (everyXDaysPattern.test(cadence)) {
    return {
      schedule: 'everyXDays',
      everyXDays: parseInt(everyXDaysPattern.exec(cadence)[1])
    }
  }

  if (onDaysInWeekPattern.test(cadence)) {
    return {
      schedule: 'onDaysInWeek',
      onDaysInWeek: onDaysInWeekPattern.exec(cadence)[1].split(',')
    }
  }

  return {
    schedule: ''
  }
}

export default {
  props: ['value'],

  data: function () {
    const cadence = this.value ? this.value.cadence : undefined
    const duration = this.value ? this.value.duration : undefined

    return Object.assign({}, {
      everyXDays: '',
      onDaysInWeek: [],
      onDaysInMonth: []
    }, interpretCadenceAndDuration(cadence, duration))
  },

  watch: {
    schedule: function (newSchedule) {
      this.$emit('input', { cadence: 'FREQ:DAILY', duration: 'P1D' })
    }
  }
}
</script>

<style>
  ul { padding-left: 0 }
  li { list-style-type: none }
</style>
