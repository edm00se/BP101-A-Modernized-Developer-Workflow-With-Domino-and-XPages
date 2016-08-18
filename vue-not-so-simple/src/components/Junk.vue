<template>
  <div class="row">
    <h3>{{ msg }}</h3>
    <ul class="list-group">
      <li v-for="j in junkAr | limitBy 20" class="list-group-item">
        <span class="badge">{{ j.id }}</span>
        {{ j.someValue }}
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data () {
    return {
      // note: changing this line won't causes changes
      // with hot-reload because the reloaded component
      // preserves its current state and we are modifying
      // its initial state.
      msg: 'A listing pulled from our NSF!',
      loading: true,
      junkAr: []
    }
  },
  ready () {
    this.$http({url: 'api.xsp/junkDrawer', method: 'GET'}).then((response) => {
      // console.log(response)
      this.$set('junkAr', (response.data.data || response.body))
      this.loading = false
    }, function (response) {
      this.$set('junkAr', null)
      console.error(response)
    })
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  color: #a1d490;
}
</style>
