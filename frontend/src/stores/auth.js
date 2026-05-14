import { defineStore } from 'pinia'
import api from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('vtc.token') || null,
    user: JSON.parse(localStorage.getItem('vtc.user') || 'null'),
    loading: false,
    error: null
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    role: (state) => state.user?.role || null,
    fullName: (state) => state.user?.fullName || 'Guest'
  },
  actions: {
    async login(email, password) {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.post('/auth/login', { email, password })
        this.token = data.token
        this.user = {
          userId: data.userId,
          email: data.email,
          fullName: data.fullName,
          role: data.role
        }
        localStorage.setItem('vtc.token', this.token)
        localStorage.setItem('vtc.user', JSON.stringify(this.user))
        return true
      } catch (err) {
        this.error = err.response?.data?.message || 'Login failed'
        return false
      } finally {
        this.loading = false
      }
    },
    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('vtc.token')
      localStorage.removeItem('vtc.user')
    }
  }
})
