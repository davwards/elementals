import RecurringTaskSubform from './recurring-task-subform'
import Vue from 'vue'
import { runGherkinUnitTests } from 'gherkin-runner'

import { testMount, select, findInput } from '../test-helpers/dom-manipulations'

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
    When I render the form with no value
    And I select "Every day"
    Then the value passed to the input handler has a daily cadence
    And the value passed to the input handler has a one-day duration

  Scenario: Selecting Every-X-Day
    When I render the form with no value
    And I select "Every X days"
    And I set X to 3
    Then the value passed to the input handler has a every-three-days cadence
    And the value passed to the input handler has a one-day duration

  Scenario: Selecting On-Certain-Days-Of-The-Week
    When I render the form with no value
    And I select "On certain days of the week"
    And I set the days to Tuesday, Thursday
    Then the value passed to the input handler has a every-tuesday-thursday cadence
    And the value passed to the input handler has a one-day duration

  Scenario: Changing X when Every-X-Days is not selected
    When I render the form with no value
    And I select "On certain days of the week"
    And I set the days to Tuesday, Thursday
    And I set X to 3
    Then the value passed to the input handler has a every-tuesday-thursday cadence

  Scenario: Changing days of the week when On-Days-In-Week is not selected
    When I render the form with no value
    And I select "Every X days"
    And I set X to 3
    And I set the days to Tuesday, Thursday
    Then the value passed to the input handler has a every-three-days cadence
`

let form;
let onInput;
let cadence;
let duration;
const allDays = [
  'Monday',
  'Tuesday',
  'Wednesday',
  'Thursday',
  'Friday',
  'Saturday',
  'Sunday'
]

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
      props: { value: { cadence, duration } },
      on: { input: onInput }
    })
  },

  "I render the form with no value": () => {
    onInput = jest.fn()
    form = testMount(RecurringTaskSubform, {
      props: { value: { cadence: '', duration: '' } },
      on: { input: onInput }
    })
  },

  "I select \"(.*)\"": async value => {
    findInput(form, value).dispatchEvent(new Event("change"))
    await Vue.nextTick()
  },

  "I set X to (\\d+)": async value => {
    const xInput = findInput(form, 'X:')
    xInput.value = value
    xInput.dispatchEvent(new Event("input", { target: xInput }))
    await Vue.nextTick()
  },

  "I set the days to (.*)": async value => {
    const selectedDays = value.split(', ')

    allDays.filter(day =>
      findInput(form, day).checked !== selectedDays.includes(day)
    ).forEach(day => {
      findInput(form, day).checked = true
      findInput(form, day).dispatchEvent(new Event("change"))
    })

    await Vue.nextTick()
  },

  "the selected schedule option is \"(.+)\"": (value) => {
    expect(findInput(form, value).checked).toBeTruthy()
  },

  "the every-x-days field is set to (\\d+)": (value) => {
    expect(findInput(everyXDaysOption(), 'X:').value).toEqual(value)
  },

  "the selected days of the week are (.*)": (value) => {
    const selectedDays = value.split(", ")
    allDays.forEach(day => {
      selectedDays.includes(day)
        ? expect(findInput(form, day).checked).toBeTruthy()
        : expect(findInput(form, day).checked).toBeFalsy()
    })
  },

  "the value passed to the input handler has a daily cadence": () => {
    expect(onInput).toHaveBeenCalled()
    expect(firstArgOfLastCall(onInput).cadence).toEqual('FREQ:DAILY')
  },

  "the value passed to the input handler has a every-three-days cadence": () => {
    expect(onInput).toHaveBeenCalled()
    expect(firstArgOfLastCall(onInput).cadence).toEqual('FREQ:DAILY;INTERVAL=3')
  },

  "the value passed to the input handler has a every-tuesday-thursday cadence": () => {
    expect(onInput).toHaveBeenCalled()
    expect(firstArgOfLastCall(onInput).cadence).toEqual('FREQ:DAILY;BYDAY=TU,TH')
  },

  "the value passed to the input handler has a one-day duration": () => {
    expect(onInput).toHaveBeenCalled()
    expect(firstArgOfLastCall(onInput).duration).toEqual('P1D')
  },

  "I log (.*)": console.log
}

function firstArgOfLastCall(mock) {
  return mock.mock.calls[mock.mock.calls.length - 1][0]
}

function everyXDaysOption () {
  return select(form, 'li').filter(li => li.innerHTML.search('Every X days') >= 0)[0]
}

runGherkinUnitTests(feature, stepDefinitions)
