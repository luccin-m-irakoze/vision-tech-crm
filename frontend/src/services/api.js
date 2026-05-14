import axios from 'axios'

/**
 * Single axios instance used by every store / component.
 * - Base URL is taken from the build-time VITE_API_BASE_URL env var.
 * - JWT (if present in localStorage) is attached to every outgoing request.
 * - 401 responses bubble up to the auth store, which clears state and
 *   redirects to the login page.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('vtc.token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('vtc.token')
      localStorage.removeItem('vtc.user')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default api
