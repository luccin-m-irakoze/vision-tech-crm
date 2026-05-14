import { defineStore } from 'pinia'
import api from '../services/api'

export const LEAD_STAGES = ['NEW', 'CONTACTED', 'QUALIFIED', 'PROPOSAL', 'CLOSED_WON', 'CLOSED_LOST']

export const useLeadStore = defineStore('lead', {
  state: () => ({
    items: [],
    loading: false,
    error: null
  }),
  getters: {
    byStage: (state) => (stage) => state.items.filter((l) => l.stage === stage)
  },
  actions: {
    async fetchAll() {
      this.loading = true
      this.error = null
      try {
        const { data } = await api.get('/leads')
        this.items = data
      } catch (err) {
        this.error = err.response?.data?.message || 'Failed to load leads'
      } finally {
        this.loading = false
      }
    },
    async create(payload) {
      const { data } = await api.post('/leads', payload)
      this.items.unshift(data)
      return data
    },
    async moveStage(id, stage) {
      const { data } = await api.put(`/leads/${id}/stage`, { stage })
      const idx = this.items.findIndex((l) => l.id === id)
      if (idx !== -1) this.items.splice(idx, 1, data)
      return data
    },
    async remove(id) {
      await api.delete(`/leads/${id}`)
      this.items = this.items.filter((l) => l.id !== id)
    }
  }
})
