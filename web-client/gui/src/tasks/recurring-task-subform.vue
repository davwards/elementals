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
        <br/>
        <label> X: <input type="number" min="2" v-model="everyXDays" /></label>
      </li>
      <li>
        <label>
          <input type="radio" name="recurring_task_schedule_type" v-model="schedule" value="onDaysInWeek"/> On certain days of the week
        </label>
        <br/>
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
import { RecurringTaskPatternInterpretations } from './recurring-task-pattern-interpretations'

const patternInterpretations = new RecurringTaskPatternInterpretations([
  {
    schedule: 'daily',
    pattern: 'FREQ:DAILY'
  }, {
    schedule: 'everyXDays',
    pattern: 'FREQ:DAILY;INTERVAL=(\\d+)',
    extract: match => parseInt(match),
    inject: value => value.toString()
  }, {
    schedule: 'onDaysInWeek',
    pattern: 'FREQ:DAILY;BYDAY=([SUMOTWEHFRA,]+)',
    extract: match => match.split(','),
    inject: value => value.join(',')
  }
])

const defaultValues = {
  everyXDays: 2,
  onDaysInWeek: []
}

export default {
  props: ['value'],

  data: function () {
    const providedValues = patternInterpretations.interpret(
      this.value ? this.value.cadence : undefined,
    )

    return Object.assign(
      {},
      defaultValues,
      providedValues
    )
  },

  watch: {
    schedule: function (newSchedule) {
      const cadenceParameter = this[newSchedule]
      this.$emit(
        'input',
        patternInterpretations.getCadenceAndDuration(newSchedule, cadenceParameter)
      )
    }
  }
}
</script>

<style>
  ul { padding-left: 0 }
  li {
    list-style-type: none;
    padding-left: 2em;
    text-indent: -2em;
    line-height: 1.5
  }
</style>
