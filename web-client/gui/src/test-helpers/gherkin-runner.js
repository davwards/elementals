function TestSuite (stepDefinitions) {
  const features = []
  let latestStepTypeAdded = null

  function currentFeature () {
    if (features.length === 0) this.addFeature(null)
    return features[features.length - 1]
  }

  function currentScenario () {
    if (currentFeature().scenarios.length === 0) this.addScenario(null)
    return currentFeature().scenarios[currentFeature().scenarios.length - 1]
  }

  function regexForStepDefinition (prefix, pattern) {
    return new RegExp(`^${prefix} ${pattern}$`)
  }

  this.addFeature = (name) => {
    features.push({
      name,
      scenarios: []
    })
  }

  this.addScenario = (name) => {
    currentFeature().scenarios.push({
      name,
      steps: {
        Given: [],
        When: [],
        Then: []
      }
    })
  }

  this.addStep = (line) => {
    const stepPrefix = line.substring(0, line.indexOf(" "))
    const stepType = stepPrefix === "And"
      ? latestStepTypeAdded
      : stepPrefix

    latestStepTypeAdded = stepType

    const matchingStepDefinition = Object.keys(stepDefinitions).filter(pattern =>
      regexForStepDefinition(stepPrefix, pattern).test(line)
    )[0]

    currentScenario().steps[stepType].push({
      text: line.substring(line.indexOf(" ") +1),
      fn: () => {
        return stepDefinitions[matchingStepDefinition](
          regexForStepDefinition(stepPrefix, matchingStepDefinition).exec(line)[1]
        )
      }
    })
  }

  function joinStepDescriptions(type, steps) {
    return steps.length === 0
      ? null
      : `${type} ${steps.map(step => step.text).join(' and ')}`
  }

  this.run = () => {
    features.forEach(feature => {
      describe(feature.name || "(unnamed feature)", () => {
        feature.scenarios.forEach(scenario => {
          describe(scenario.name || "(unnamed scenario)", () => {
            beforeEach(async () => {
              await scenario.steps.Given
                .reduce((chain, next) => chain.then(next.fn), Promise.resolve())
              await scenario.steps.When
                .reduce((chain, next) => chain.then(next.fn), Promise.resolve())
            })

            scenario.steps.Then.forEach(thenStep => {
              const description = [
                joinStepDescriptions('given', scenario.steps.Given),
                joinStepDescriptions('when', scenario.steps.When),
                joinStepDescriptions('then', [thenStep])
              ].filter(segment => !!segment)
              .join(', ')

              test(description[0].toUpperCase() + description.substring(1), thenStep.fn)
            })
          })
        })
      })
    })
  }
}

export function runGherkinUnitTests (gherkin, stepDefinitions) {
  const isNotABlankLine = line => !/^\s*$/.test(line)
  const isNotAComment = line => !/^\s*#/.test(line)

  const testSuite = gherkin.split('\n')
    .filter(isNotABlankLine)
    .filter(isNotAComment)
    .map(line => line.trim())
    .reduce((suite, line) => {
      if (line.startsWith('Feature: ')) {
        suite.addFeature(/^Feature: (.*)$/.exec(line)[1])
      } else if (line.startsWith('Scenario: ')) {
        suite.addScenario(/^Scenario: (.*)$/.exec(line)[1])
      } else {
        suite.addStep(line)
      }

      return suite
    }, new TestSuite(stepDefinitions))

  testSuite.run()
}
