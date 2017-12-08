function PatternInterpretation (schedule, pattern, extractField, injectField) {
  this.schedule = schedule
  this.patternMatches = value => new RegExp(`^${pattern}$`).test(value)
  this.extract = value => extractField(new RegExp(`^${pattern}$`).exec(value)[1])
  this.inject = value => pattern.replace(/\(.*\)/, injectField(value))
  this.interpret = cadence => ({
    schedule: schedule,
    [schedule]: this.extract(cadence)
  })
}

export function RecurringTaskPatternInterpretations (interpretationDetails) {
  const interpretations = interpretationDetails.map(detail =>
    new PatternInterpretation(
      detail.schedule,
      detail.pattern,
      detail.extract || (() => {}),
      detail.inject || (() => {})
    )
  )

  const findCorrespondenceWhere = predicate => interpretations.filter(predicate)[0]

  const correspondenceMatching = match =>
    findCorrespondenceWhere(correspondence => correspondence.patternMatches(match))

  const correspondenceWithSchedule = schedule =>
    findCorrespondenceWhere(correspondence => correspondence.schedule === schedule)

  this.interpret = cadence => {
    if (!correspondenceMatching(cadence)) return { schedule: '' }

    return correspondenceMatching(cadence).interpret(cadence)
  }

  this.getCadenceAndDuration = (schedule, cadenceValue) => ({
    cadence: correspondenceWithSchedule(schedule).inject(cadenceValue),
    duration: 'P1D'
  })
}
