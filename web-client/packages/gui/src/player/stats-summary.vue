<template>
  <section>
    <h3>Your stats:</h3>
    <dl>
      <dt>Health:</dt>
      <dd>{{player.health}}</dd>

      <dt>Experience:</dt>
      <dd>{{player.experience}}</dd>

      <dt>Coin:</dt>
      <dd>{{player.coin}}</dd>
    </dl>
  </section>
</template>

<script>
export default {
  props: ['player']
}
</script>
