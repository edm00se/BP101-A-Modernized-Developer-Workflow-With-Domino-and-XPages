import Vue from 'vue'
import VueResource from 'vue-resource'
import App from './App'

Vue.use(VueResource)

/**
 * Load Vue HTTP Interceptors.
 */
Vue.http.interceptors.push((request, next) => {
  if (process.env.NODE_ENV === 'development') {
    return require('./interceptors')(request, next)
  } else {
    return next()
  }
})

/* eslint-disable no-new */
new Vue({
  el: 'body',
  components: { App }
})
