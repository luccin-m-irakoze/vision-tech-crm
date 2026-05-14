<template>
  <AppShell>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 mb-6">
      <div>
        <h1 class="page-title">Dashboard</h1>
        <p class="page-subtitle">A live snapshot of your CRM activity.</p>
      </div>
      <div class="flex items-center gap-2">
        <span class="badge-green">
          <span class="h-1.5 w-1.5 rounded-full bg-emerald-500"></span>
          Live
        </span>
        <button class="btn-secondary btn-sm" @click="loadAll">
          <ArrowPathIcon class="h-4 w-4" :class="{ 'animate-spin': loading }" />
          Refresh
        </button>
      </div>
    </div>

    <LoadingSpinner v-if="loading" />
    <ErrorBanner v-else-if="error" :message="error" />

    <div v-else class="space-y-6">
      <!-- KPI cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          label="Total customers"
          :value="summary.totalCustomers"
          :icon="UsersIcon"
          tone="blue" />
        <StatCard
          label="Open leads"
          :value="summary.openLeads"
          :icon="RectangleStackIcon"
          tone="amber" />
        <StatCard
          label="Open tickets"
          :value="summary.openTickets"
          :icon="LifebuoyIcon"
          tone="red" />
        <StatCard
          label="Closed-won leads"
          :value="summary.closedWonLeads"
          :icon="TrophyIcon"
          tone="green" />
      </div>

      <!-- Charts row -->
      <div class="grid grid-cols-1 lg:grid-cols-5 gap-6">
        <!-- Leads by stage (bars) -->
        <div class="card card-hover lg:col-span-3">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h2 class="section-title">Leads by stage</h2>
              <p class="text-xs text-slate-500 mt-0.5">Current pipeline distribution</p>
            </div>
            <span class="badge-purple">
              <RectangleStackIcon class="h-3 w-3" />
              Pipeline
            </span>
          </div>
          <ul class="space-y-3">
            <li v-for="(count, stage) in summary.leadsByStage" :key="stage">
              <div class="flex items-center justify-between text-xs mb-1">
                <span class="font-medium text-slate-700">{{ niceStage(stage) }}</span>
                <span class="font-bold text-vtc-ink">{{ count }}</span>
              </div>
              <div class="h-2 rounded-full bg-slate-100 overflow-hidden">
                <div
                  class="h-full rounded-full bg-gradient-to-r from-vtc-accent to-vtc-purple
                         transition-all duration-700"
                  :style="{ width: pctWidth(count) + '%' }">
                </div>
              </div>
            </li>
            <li v-if="!Object.keys(summary.leadsByStage || {}).length"
                class="text-sm text-slate-500">No leads yet.</li>
          </ul>
        </div>

        <!-- Recent interactions -->
        <div class="card card-hover lg:col-span-2">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h2 class="section-title">Recent interactions</h2>
              <p class="text-xs text-slate-500 mt-0.5">Latest customer touchpoints</p>
            </div>
            <ChatBubbleLeftRightIcon class="h-5 w-5 text-slate-400" />
          </div>
          <LoadingSpinner v-if="loadingInteractions" />
          <ul v-else class="divide-y divide-slate-100 -my-2">
            <li v-for="i in interactions" :key="i.id" class="py-3">
              <div class="flex gap-3">
                <div class="h-9 w-9 rounded-lg shrink-0 flex items-center justify-center"
                     :class="iconBg(i.type)">
                  <component :is="iconFor(i.type)" class="h-4 w-4 text-white" />
                </div>
                <div class="min-w-0 flex-1">
                  <div class="flex items-center justify-between gap-2">
                    <span class="font-semibold text-sm text-vtc-ink truncate">{{ i.subject }}</span>
                    <span class="text-[11px] text-slate-400 shrink-0">{{ formatDate(i.occurredAt) }}</span>
                  </div>
                  <div class="text-xs text-slate-500 truncate">
                    {{ niceType(i.type) }} · {{ i.customerName }} · by {{ i.userName || 'System' }}
                  </div>
                </div>
              </div>
            </li>
            <li v-if="!interactions.length" class="py-6 text-center text-sm text-slate-500">
              No interactions yet.
            </li>
          </ul>
        </div>
      </div>
    </div>
  </AppShell>
</template>

<script setup>
import { computed, h, onMounted, ref } from 'vue'
import AppShell from '../components/AppShell.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorBanner from '../components/ErrorBanner.vue'
import api from '../services/api'
import {
  UsersIcon, RectangleStackIcon, LifebuoyIcon, TrophyIcon,
  ArrowPathIcon, ChatBubbleLeftRightIcon, PhoneIcon, EnvelopeIcon,
  CalendarDaysIcon, DocumentTextIcon, ArrowTrendingUpIcon, ArrowTrendingDownIcon
} from '@heroicons/vue/24/outline'

const summary = ref({})
const interactions = ref([])
const loading = ref(true)
const loadingInteractions = ref(true)
const error = ref('')

const TONES = {
  blue:   { bg: 'from-blue-500/15 to-blue-500/5',         icon: 'bg-blue-500',    ring: 'ring-blue-200',    text: 'text-blue-700' },
  amber:  { bg: 'from-amber-500/15 to-amber-500/5',       icon: 'bg-amber-500',   ring: 'ring-amber-200',   text: 'text-amber-700' },
  red:    { bg: 'from-rose-500/15 to-rose-500/5',         icon: 'bg-rose-500',    ring: 'ring-rose-200',    text: 'text-rose-700' },
  green:  { bg: 'from-emerald-500/15 to-emerald-500/5',   icon: 'bg-emerald-500', ring: 'ring-emerald-200', text: 'text-emerald-700' }
}

const StatCard = {
  props: ['label', 'value', 'icon', 'tone'],
  setup (props) {
    const tone = computed(() => TONES[props.tone] || TONES.blue)
    return () => h('div', {
      class: `card card-hover relative overflow-hidden`
    }, [
      h('div', { class: `absolute inset-0 bg-gradient-to-br ${tone.value.bg} pointer-events-none` }),
      h('div', { class: 'relative flex items-start justify-between' }, [
        h('div', null, [
          h('div', { class: 'text-[11px] font-semibold uppercase tracking-widest text-slate-500' }, props.label),
          h('div', { class: 'mt-2 text-3xl font-display font-bold text-vtc-ink' }, props.value ?? '—')
        ]),
        h('div', {
          class: `h-10 w-10 rounded-xl ${tone.value.icon} text-white flex items-center justify-center shadow-soft`
        }, [
          h(props.icon, { class: 'h-5 w-5' })
        ])
      ]),
      h('div', { class: `relative mt-3 inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[11px] font-semibold bg-white/70 ${tone.value.text} ring-1 ring-inset ${tone.value.ring}` }, [
        h(ArrowTrendingUpIcon, { class: 'h-3 w-3' }),
        ' Live'
      ])
    ])
  }
}

const TYPE_META = {
  CALL:    { icon: PhoneIcon,            bg: 'bg-blue-500' },
  EMAIL:   { icon: EnvelopeIcon,         bg: 'bg-purple-500' },
  MEETING: { icon: CalendarDaysIcon,     bg: 'bg-emerald-500' },
  NOTE:    { icon: DocumentTextIcon,     bg: 'bg-slate-500' },
  CHAT:    { icon: ChatBubbleLeftRightIcon, bg: 'bg-teal-500' }
}
function iconFor (t) { return TYPE_META[t]?.icon || DocumentTextIcon }
function iconBg (t) { return TYPE_META[t]?.bg || 'bg-slate-400' }
function niceType (t) { return (t || '').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase()) }

function niceStage (s) {
  return (s || '').replace('_', ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase())
}

function pctWidth (count) {
  const values = Object.values(summary.value?.leadsByStage || {})
  const max = Math.max(...values, 1)
  return Math.max(8, Math.round((count / max) * 100))
}

function formatDate (d) {
  if (!d) return ''
  try { return new Date(d).toLocaleString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) }
  catch { return d }
}

async function loadAll () {
  try {
    loading.value = true
    const { data } = await api.get('/reports/summary')
    summary.value = data
  } catch (err) {
    error.value = err.response?.data?.message || 'Could not load summary'
  } finally {
    loading.value = false
  }
  try {
    loadingInteractions.value = true
    const { data } = await api.get('/interactions')
    interactions.value = data.slice(0, 8)
  } catch {
    interactions.value = []
  } finally {
    loadingInteractions.value = false
  }
}

onMounted(loadAll)
</script>
