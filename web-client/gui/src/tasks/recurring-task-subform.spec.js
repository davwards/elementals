import RecurringTaskSubform from './recurring-task-subform'
import Vue from 'vue'

import { testMount, select, findInput } from '../test-helpers/dom-manipulations'
import { runGherkinUnitTests } from '../test-helpers/gherkin-runner'

const feature = `

Feature: RecurringTaskSubform

  Scenario: Daily cadence
    Given a daily cadence
    And a one-day duration
    When I render the form
    Then the selected schedule option is "Every day"

  Scenario: Every-X-Days cadence (2 days)
    Given an every-other-day cadence
    And a one-day duration
    When I render the form
    Then the selected schedule option is "Every X days"
    And the every-x-days field is set to 2

  Scenario: Every-X-Days cadence (14 days)
    Given an every-14-days cadence
    And a one-day duration
    When I render the form
    Then the selected schedule option is "Every X days"
    And the every-x-days field is set to 14

  Scenario: On-Days-In-Week cadence (Sundays)
    Given an every-Sunday cadence
    And a one-day duration
    When I render the form
    Then the selected schedule option is "On certain days of the week"
    And the selected days of the week are Sunday

  Scenario: On-Days-In-Week cadence (Monday-Wednesday-Friday)
    Given a monday-wednesday-friday cadence
    And a one-day duration
    When I render the form
    Then the selected schedule option is "On certain days of the week"
    And the selected days of the week are Monday, Wednesday, Friday

  Scenario: Selecting Every-Day
    When I render the form
    And I select "Every day"
    Then the value passed to the input handler has a daily cadence
    And the value passed to the input handler has a one-day duration
`

let form;
let onInput;
let cadence;
let duration;

const stepDefinitions = {
  "a daily cadence": () => {
    cadence = 'FREQ:DAILY'
  },

  "an every-other-day cadence": () => {
    cadence = 'FREQ:DAILY;INTERVAL=2'
  },

  "an every-14-days cadence": () => {
    cadence = 'FREQ:DAILY;INTERVAL=14'
  },

  "an every-Sunday cadence": () => {
    cadence = 'FREQ:DAILY;BYDAY=SU'
  },

  "a monday-wednesday-friday cadence": () => {
    cadence = 'FREQ:DAILY;BYDAY=MO,WE,FR'
  },

  "a one-day duration": () => {
    duration = 'P1D'
  },

  "I render the form": () => {
    onInput = jest.fn()
    form = testMount(RecurringTaskSubform, {
      props: { value: { cadence: cadence || '', duration: duration || '' } },
      on: { input: onInput }
    })
  },

  "I select \"(.*)\"": (value) => {
    findInput(form, value).dispatchEvent(new Event("change"))
  },

  "the selected schedule option is \"(.+)\"": (value) => {
    expect(findInput(form, value).checked).toBeTruthy()
  },

  "the every-x-days field is set to (\\d+)": (value) => {
    expect(findInput(everyXDaysOption(), 'X:').value).toEqual(value)
  },

  "the selected days of the week are (.*)": (value) => {
    const selectedDays = value.split(", ")
    const allDays = [
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday',
      'Saturday',
      'Sunday'
    ]

    allDays.forEach(day => {
      selectedDays.includes(day)
        ? expect(findInput(form, day).checked).toBeTruthy()
        : expect(findInput(form, day).checked).toBeFalsy()
    })
  },

  "the value passed to the input handler has a daily cadence": async () => {
    await Vue.nextTick()
    expect(onInput).toHaveBeenCalled()
    expect(onInput.mock.calls[0][0].cadence).toEqual('FREQ:DAILY')
  },

  "the value passed to the input handler has a one-day duration": async () => {
    await Vue.nextTick()
    expect(onInput).toHaveBeenCalled()
    expect(onInput.mock.calls[0][0].duration).toEqual('P1D')
  }
}

function everyXDaysOption () {
  return select(form, 'li').filter(li => li.innerHTML.search('Every X days') >= 0)[0]
}

runGherkinUnitTests(feature, stepDefinitions)
