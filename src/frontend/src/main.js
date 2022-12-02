import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import capitalize from './plugins/capitalize'
import store from './store'

createApp(App).use(store).use(router).use(capitalize).mount('#app')
