<template>
  <AppShell>
    <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 mb-6">
      <div>
        <h1 class="page-title">Reports</h1>
        <p class="page-subtitle">Trends and breakdowns across your CRM activity.</p>
      </div>
      <button class="btn-secondary" @click="exportCsv" :disabled="!summary">
        <ArrowDownTrayIcon class="h-4 w-4" />
        Export CSV
      </button>
    </div>

    <LoadingSpinner v-if="loading" />
    <ErrorBanner v-else-if="error" :message="error" />

    <div v-else class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="card card-hover">
        <div class="flex items-center justify-between mb-4">
          <div>
            <h2 class="section-title">Monthly leads</h2>
            <p class="text-xs text-slate-500 mt-0.5">New leads created per month</p>
          </div>
          <span class="badge-blue">
            <ChartBarIcon class="h-3 w-3" />
            Trend
          </span>
        </div>
        <div class="h-72">
          <Bar v-if="monthlyData" :data="monthlyData" :options="barOptions" />
          <p v-else class="text-sm text-slate-500">No data.</p>
        </div>
      </div>
      <div class="card card-hover">
        <div class="flex items-center justify-between mb-4">
          <div>
            <h2 class="section-title">Tickets by status</h2>
            <p class="text-xs text-slate-500 mt-0.5">Distribution of support tickets</p>
          </div>
          <span class="badge-purple">
            <ChartPieIcon class="h-3 w-3" />
            Breakdown
          </span>
        </div>
        <div class="h-72">
          <Doughnut v-if="ticketData" :data="ticketData" :options="doughnutOptions" />
          <p v-else class="text-sm text-slate-500">No data.</p>
        </div>
      </div>
    </div>
  </AppShell>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Bar, Doughnut } from 'vue-chartjs'
import {
  Chart, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement
} from 'chart.js'
import AppShell from '../components/AppShell.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorBanner from '../components/ErrorBanner.vue'
import api from '../services/api'
import {
  ArrowDownTrayIcon, ChartBarIcon, ChartPieIcon
} from '@heroicons/vue/24/outline'

Chart.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement)

const summary = ref(null)
const loading = ref(true)
const error = ref('')

const monthlyData = computed(() => {
  if (!summary.value?.monthlyLeads?.length) return null
  return {
    labels: summary.value.monthlyLeads.map((m) => m.month),
    datasets: [{
      label: 'New leads',
      data: summary.value.monthlyLeads.map((m) => m.count),
      backgroundColor: (ctx) => {
        const chart = ctx.chart
        const { ctx: c, chartArea } = chart
        if (!chartArea) return '#1E88E5'
        const grad = c.createLinearGradient(0, chartArea.bottom, 0, chartArea.top)
        grad.addColorStop(0, '#2563EB')
        grad.addColorStop(1, '#7C3AED')
        return grad
      },
      borderRadius: 8,
      barThickness: 22
    }]
  }
})

const ticketData = computed(() => {
  if (!summary.value?.ticketsByStatus) return null
  const labels = Object.keys(summary.value.ticketsByStatus)
  const values = Object.values(summary.value.ticketsByStatus)
  return {
    labels,
    datasets: [{
      data: values,
      backgroundColor: ['#94A3B8', '#1E88E5', '#F59E0B', '#10B981', '#7C3AED'],
      borderWidth: 0,
      hoverOffset: 6
    }]
  }
})

const barOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false } },
  scales: {
    x: { grid: { display: false }, ticks: { color: '#64748B' } },
    y: { grid: { color: '#E2E8F0' }, ticks: { color: '#64748B' }, beginAtZero: true }
  }
}

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  cutout: '60%',
  plugins: {
    legend: { position: 'bottom', labels: { color: '#475569', boxWidth: 10, padding: 12 } }
  }
}

function exportCsv () {
  if (!summary.value) return
  const rows = []
  rows.push(['Metric', 'Value'])
  rows.push(['Total customers', summary.value.totalCustomers])
  rows.push(['Open leads', summary.value.openLeads])
  rows.push(['Open tickets', summary.value.openTickets])
  rows.push(['Closed-won leads', summary.value.closedWonLeads])
  for (const [k, v] of Object.entries(summary.value.leadsByStage || {})) rows.push([`Leads/${k}`, v])
  for (const [k, v] of Object.entries(summary.value.ticketsByStatus || {})) rows.push([`Tickets/${k}`, v])
  const csv = rows.map((r) => r.join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = 'vtc-crm-report.csv'
  a.click()
}

onMounted(async () => {
  loading.value = true
  try {
    const { data } = await api.get('/reports/summary')
    summary.value = data
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load reports'
  } finally {
    loading.value = false
  }
})
</script>
