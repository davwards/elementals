<template>
  <article>
    <h1>Welcome, newcomer!</h1>
    <h2>Let's create your player.</h2>
    <form @submit.prevent="submit">
      <label>Player Name: <input type="text" name="player_name" v-model="playerName"/></label>
      <input type="submit" value="Create Player"/>
    </form>
  </article>
</template>

<script>
export default {
  name: 'signup-dialogue',

  inject: ['gameEngine'],

  data: function () {
    return {
      playerName: ''
    }
  },

  methods: {
    submit: function () {
      this.gameEngine.player.create(this.playerName)
    }
  }
}
</script>
