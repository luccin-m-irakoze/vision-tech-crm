import { defineStore } from 'pinia'
import api from '../services/api'

export const useCustomerStore = defineStore('customer', {
  state: () => ({
    items: [],
    current: null,
    loading: false,
    error: null
  }),
  actions: {
    async fetchAll(query = '') {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.get('/customers', { params: query ? { q: query } : {} })
        this.items = data
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to load customers'
      } finally {
        this.loading = false
      }
    },
    async fetchOne(id) {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.get(`/customers/${id}`)
        this.current = data
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to load customer'
      } finally {
        this.loading = false
      }
    },
    async create(payload) {
      const { data } = await api.post('/customers', payload)
      this.items.unshift(data)
      return data
    },
    async update(id, payload) {
      const { data } = await api.put(`/customers/${id}`, payload)
      const idx = this.items.findIndex((c) => c.id === id)
      if (idx !== -1) this.items.splice(idx, 1, data)
      return data
    },
    async remove(id) {
      await api.delete(`/customers/${id}`)
      this.items = this.items.filter((c) => c.id !== id)
    }
  }
})
