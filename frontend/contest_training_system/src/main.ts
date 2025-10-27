import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router/index'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { useUserStore } from './store/user'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)
 
// 应用启动时自动获取用户信息
const userStore = useUserStore()
if (userStore.token) {
  userStore.fetchUserProfile().catch(() => {
    // 如果获取用户信息失败，清除token
    userStore.logout()
  })
}

app.mount('#app')
