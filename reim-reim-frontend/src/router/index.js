import { createRouter, createWebHistory } from 'vue-router'
import MainView from '@/views/MainView.vue'
import FormView from '@/views/FormView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: MainView
    },
    {
      path: '/form',
      name: 'form',
      component: FormView
    }
  ]
})

export default router
