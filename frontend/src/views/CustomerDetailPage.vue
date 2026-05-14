<template>
  <AppShell>
    <button class="btn-ghost btn-sm mb-4 -ml-2" @click="$router.push('/customers')">
      <ArrowLeftIcon class="h-4 w-4" />
      Back to customers
    </button>

    <LoadingSpinner v-if="store.loading" />
    <ErrorBanner v-else-if="store.error" :message="store.error" />

    <div v-else-if="store.current" class="space-y-6">
      <CustomerCard :customer="store.current" />

      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-2">
            <ClockIcon class="h-5 w-5 text-vtc-purple" />
            <h2 class="section-title">Interaction history</h2>
          </div>
          <span class="badge-slate">{{ history.length }} entries</span>
        </div>
        <LoadingSpinner v-if="loadingHistory" />
        <ul v-else class="space-y-1">
          <li v-for="i in history" :key="i.id"
              class="flex gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors">
            <div class="h-9 w-9 rounded-lg shrink-0 flex items-center justify-center"
                 :class="iconBg(i.type)">
              <component :is="iconFor(i.type)" class="h-4 w-4 text-white" />
            </div>
            <div class="min-w-0 flex-1">
              <div class="flex items-center justify-between gap-2">
                <span class="font-semibold text-sm text-vtc-ink truncate">{{ i.subject }}</span>
                <span class="text-[11px] text-slate-400 shrink-0">{{ format(i.occurredAt) }}</span>
              </div>
              <div class="text-xs text-slate-500">
                {{ niceType(i.type) }} · by {{ i.userName || 'System' }}
              </div>
              <p v-if="i.notes" class="text-sm text-slate-700 mt-1 leading-relaxed">{{ i.notes }}</p>
            </div>
          </li>
          <li v-if="!history.length" class="text-center py-8 text-sm text-slate-500">
            No interactions recorded.
          </li>
        </ul>
      </div>
    </div>
  </AppShell>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppShell from '../components/AppShell.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorBanner from '../components/ErrorBanner.vue'
import CustomerCard from '../components/CustomerCard.vue'
import { useCustomerStore } from '../stores/customer'
import api from '../services/api'
import {
  ArrowLeftIcon, ClockIcon, PhoneIcon, EnvelopeIcon,
  CalendarDaysIcon, MapPinIcon, DocumentTextIcon, ChatBubbleLeftRightIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const store = useCustomerStore()
const history = ref([])
const loadingHistory = ref(false)

const TYPE_META = {
  CALL:       { icon: PhoneIcon,            bg: 'bg-blue-500' },
  EMAIL:      { icon: EnvelopeIcon,         bg: 'bg-purple-500' },
  MEETING:    { icon: CalendarDaysIcon,     bg: 'bg-emerald-500' },
  SITE_VISIT: { icon: MapPinIcon,           bg: 'bg-rose-500' },
  NOTE:       { icon: DocumentTextIcon,     bg: 'bg-slate-500' },
  CHAT:       { icon: ChatBubbleLeftRightIcon, bg: 'bg-teal-500' }
}
function iconFor (t) { return TYPE_META[t]?.icon || DocumentTextIcon }
function iconBg (t) { return TYPE_META[t]?.bg || 'bg-slate-400' }
function niceType (t) { return (t || '').replace('_', ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase()) }

function format (d) { try { return new Date(d).toLocaleString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) } catch { return d } }

async function load (id) {
  await store.fetchOne(id)
  loadingHistory.value = true
  try {
    const { data } = await api.get('/interactions', { params: { customerId: id } })
    history.value = data
  } catch { history.value = [] }
  finally { loadingHistory.value = false }
}

onMounted(() => load(route.params.id))
watch(() => route.params.id, (id) => id && load(id))
</script>
