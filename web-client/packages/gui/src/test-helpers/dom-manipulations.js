import Vue from 'vue'

export const testMount = (Component, data) => new Vue({
  render: createElement => createElement(Component, data)
}).$mount().$el

export const select = (el, selector) =>
  Array.prototype.slice.call(el.querySelectorAll(selector))

export function findInput (el, labelText) {
  const label = select(el, 'label')
    .filter(label =>
      label.innerHTML.search(labelText) >= 0
    )[0]

  if (!label) throw new Error('No label found containing text ' + labelText)

  return label.querySelector('input')
}
