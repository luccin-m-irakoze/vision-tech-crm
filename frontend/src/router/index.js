import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import LoginPage from '../views/LoginPage.vue'
import DashboardPage from '../views/DashboardPage.vue'
import CustomersPage from '../views/CustomersPage.vue'
import CustomerDetailPage from '../views/CustomerDetailPage.vue'
import LeadsPage from '../views/LeadsPage.vue'
import InteractionsPage from '../views/InteractionsPage.vue'
import TicketsPage from '../views/TicketsPage.vue'
import ReportsPage from '../views/ReportsPage.vue'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', name: 'login', component: LoginPage, meta: { public: true } },
  { path: '/dashboard', name: 'dashboard', component: DashboardPage },
  { path: '/customers', name: 'customers', component: CustomersPage },
  { path: '/customers/:id', name: 'customer-detail', component: CustomerDetailPage, props: true },
  { path: '/leads', name: 'leads', component: LeadsPage },
  { path: '/interactions', name: 'interactions', component: InteractionsPage },
  { path: '/tickets', name: 'tickets', component: TicketsPage },
  { path: '/reports', name: 'reports', component: ReportsPage },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && auth.isAuthenticated) {
    return { name: 'dashboard' }
  }
})

export default router
