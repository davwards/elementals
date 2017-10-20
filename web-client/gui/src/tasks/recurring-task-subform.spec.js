import RecurringTaskSubform from './recurring-task-subform'
import Vue from 'vue'

describe('RecurringTaskSubform', () => {
  describe('with a daily-cadence value', () => {
    it('selects the Every Day option', () => {
      whenTheFormIsRenderedWith({ cadence: 'FREQ:DAILY', duration: 'P1D' })
      thenTheSelectedScheduleOptionIs('Every day')
    })
  })

  describe('with a every-x-days value', () => {
    it('selects the Every X Days option', () => {
      whenTheFormIsRenderedWith({ cadence: 'FREQ:DAILY;INTERVAL=2', duration: 'P1D' })
      thenTheSelectedScheduleOptionIs('Every X days')
      expect(findInput(everyXDaysOption(), 'X:').value).toEqual('2')

      whenTheFormIsRenderedWith({ cadence: 'FREQ:DAILY;INTERVAL=14', duration: 'P1D' })
      thenTheSelectedScheduleOptionIs('Every X days')
      expect(findInput(everyXDaysOption(), 'X:').value).toEqual('14')
    })
  })

  describe('with a on-days-in-week value', () => {
    it('selects the On Certain Days of the Week option', () => {
      whenTheFormIsRenderedWith({ cadence: 'FREQ:DAILY;BYDAY=SU', duration: 'P1D' })
      thenTheSelectedScheduleOptionIs('On certain days of the week')
      thenTheSelectedDaysOfTheWeekIs('Sunday')

      whenTheFormIsRenderedWith({ cadence: 'FREQ:DAILY;BYDAY=MO,WE,FR', duration: 'P1D' })
      thenTheSelectedScheduleOptionIs('On certain days of the week')
      thenTheSelectedDaysOfTheWeekIs('Monday', 'Wednesday', 'Friday')
    })
  })

  describe('when the Every Day option is selected', () => {
    it('reports a daily-cadence value', () => {
      whenTheFormIsRenderedWith({ cadence: '', duration: '' })
      whenEveryDayIsChosen()

      return thenOnInputHandlerIsCalledWith({
        cadence: 'FREQ:DAILY',
        duration: 'P1D'
      })
    })
  })

  let form;
  let onInput;

  const testMount = (Component, data) => new Vue({
    render: createElement => createElement(Component, data)
  }).$mount().$el

  const select = (el, selector) => Array.prototype.slice.call(el.querySelectorAll(selector))

  function findInput (el, labelText) {
    const label = select(el, 'label')
      .filter(label =>
        label.innerHTML.search(labelText) >= 0
      )[0]

    if (!label) throw new Error('No label found containing text ' + labelText)

    return label.querySelector('input')
  }

  function whenTheFormIsRenderedWith (value) {
    onInput = jest.fn()
    form = testMount(RecurringTaskSubform, {
      props: { value },
      on: { input: onInput }
    })
  }

  function whenEveryDayIsChosen () {
    findInput(form, 'Every day').dispatchEvent(new Event("change"))
  }

  function thenTheSelectedScheduleOptionIs (selectedOption) {
    expect(findInput(form, selectedOption).checked).toBeTruthy()
  }

  function thenTheSelectedDaysOfTheWeekIs (...selectedDays) {
    [
      'Monday',
      'Tuesday',
      'Wednesday',
      'Thursday',
      'Friday',
      'Saturday',
      'Sunday'
    ].forEach(day => {
      selectedDays.includes(day)
        ? expect(findInput(form, day).checked).toBeTruthy()
        : expect(findInput(form, day).checked).toBeFalsy()
    })
  }

  function thenOnInputHandlerIsCalledWith(value) {
    return Vue.nextTick().then(() => {
      expect(onInput).toHaveBeenCalledWith(value)
    })
  }

  function everyXDaysOption () {
    return select(form, 'li').filter(li => li.innerHTML.search('Every X days') >= 0)[0]
  }

})
