import Vue from 'vue'
import Hello from './Hello.vue'

describe('Hello', () => {
  it('works', () => {
    const Component = Vue.extend(Hello)
    const vm = new Component().$mount()
    expect(vm.$el.textContent).toContain('Welcome')
  })
})
