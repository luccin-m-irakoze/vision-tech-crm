<template>
  <AppShell>
    <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 mb-6">
      <div>
        <h1 class="page-title">Interactions</h1>
        <p class="page-subtitle">Log calls, e-mails and meetings with your customers.</p>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Form -->
      <div class="card lg:col-span-1 self-start">
        <div class="flex items-center gap-2 mb-4">
          <PencilSquareIcon class="h-5 w-5 text-vtc-accent" />
          <h2 class="section-title">Log new activity</h2>
        </div>
        <ErrorBanner :message="formError" class="mb-4" />
        <form class="space-y-4" novalidate @submit.prevent="onSubmit">
          <div>
            <label class="label">Customer *</label>
            <select v-model="form.customerId" class="input" :class="{ 'input-error': errors.customerId }">
              <option value="">— select —</option>
              <option v-for="c in customers.items" :key="c.id" :value="c.id">{{ c.companyName }}</option>
            </select>
            <p v-if="errors.customerId" class="text-xs text-red-600 mt-1">{{ errors.customerId }}</p>
          </div>
          <div>
            <label class="label">Type *</label>
            <div class="grid grid-cols-2 gap-2">
              <button
                v-for="t in types" :key="t.value"
                type="button"
                class="flex items-center gap-2 px-3 py-2 rounded-lg border text-sm
                       transition-all"
                :class="form.type === t.value
                  ? 'border-vtc-accent bg-vtc-accent/5 text-vtc-accent2 ring-2 ring-vtc-accent/20'
                  : 'border-slate-200 text-slate-600 hover:border-slate-300'"
                @click="form.type = t.value">
                <component :is="t.icon" class="h-4 w-4" />
                {{ t.label }}
              </button>
            </div>
          </div>
          <div>
            <label class="label">Subject *</label>
            <input v-model="form.subject" class="input" :class="{ 'input-error': errors.subject }" />
            <p v-if="errors.subject" class="text-xs text-red-600 mt-1">{{ errors.subject }}</p>
          </div>
          <div>
            <label class="label">Notes</label>
            <textarea v-model="form.notes" rows="4" class="input" placeholder="What was discussed?"></textarea>
          </div>
          <button type="submit" class="btn-primary w-full" :disabled="saving">
            <ArrowPathIcon v-if="saving" class="h-4 w-4 animate-spin" />
            <CheckIcon v-else class="h-4 w-4" />
            {{ saving ? 'Saving…' : 'Save interaction' }}
          </button>
        </form>
      </div>

      <!-- History -->
      <div class="card lg:col-span-2">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-2">
            <ClockIcon class="h-5 w-5 text-vtc-purple" />
            <h2 class="section-title">Recent activity</h2>
          </div>
          <span class="badge-slate">{{ items.length }} total</span>
        </div>
        <LoadingSpinner v-if="loading" />
        <ErrorBanner v-else-if="error" :message="error" />
        <ul v-else class="space-y-1">
          <li v-for="i in items" :key="i.id"
              class="flex gap-3 p-3 rounded-xl hover:bg-slate-50 transition-colors">
            <div class="h-10 w-10 rounded-lg shrink-0 flex items-center justify-center"
                 :class="iconBg(i.type)">
              <component :is="iconFor(i.type)" class="h-5 w-5 text-white" />
            </div>
            <div class="min-w-0 flex-1">
              <div class="flex items-center justify-between gap-2">
                <span class="font-semibold text-sm text-vtc-ink truncate">{{ i.subject }}</span>
                <span class="text-[11px] text-slate-400 shrink-0">{{ format(i.occurredAt) }}</span>
              </div>
              <div class="text-xs text-slate-500 truncate">
                {{ niceType(i.type) }} · {{ i.customerName }} · by {{ i.userName || 'System' }}
              </div>
              <p v-if="i.notes" class="text-sm text-slate-700 mt-1.5 leading-relaxed">{{ i.notes }}</p>
            </div>
          </li>
          <li v-if="!items.length"
              class="text-center py-10 text-sm text-slate-500">
            No interactions yet. Log your first one on the left.
          </li>
        </ul>
      </div>
    </div>
  </AppShell>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppShell from '../components/AppShell.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorBanner from '../components/ErrorBanner.vue'
import { useCustomerStore } from '../stores/customer'
import { useAuthStore } from '../stores/auth'
import api from '../services/api'
import {
  PhoneIcon, EnvelopeIcon, CalendarDaysIcon, MapPinIcon,
  DocumentTextIcon, ChatBubbleLeftRightIcon, PencilSquareIcon,
  ClockIcon, ArrowPathIcon, CheckIcon
} from '@heroicons/vue/24/outline'

const customers = useCustomerStore()
const auth = useAuthStore()

const items = ref([])
const loading = ref(true)
const error = ref('')
const saving = ref(false)
const formError = ref('')

const empty = () => ({ customerId: '', type: 'CALL', subject: '', notes: '' })
const form = reactive(empty())
const errors = reactive({ customerId: '', subject: '' })

const types = [
  { value: 'CALL',       label: 'Call',       icon: PhoneIcon },
  { value: 'EMAIL',      label: 'E-mail',     icon: EnvelopeIcon },
  { value: 'MEETING',    label: 'Meeting',    icon: CalendarDaysIcon },
  { value: 'SITE_VISIT', label: 'Site visit', icon: MapPinIcon }
]

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

async function refresh () {
  loading.value = true
  error.value = ''
  try {
    const { data } = await api.get('/interactions')
    items.value = data
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load interactions'
  } finally {
    loading.value = false
  }
}

function validate () {
  errors.customerId = ''
  errors.subject = ''
  let ok = true
  if (!form.customerId) { errors.customerId = 'Select a customer'; ok = false }
  if (!form.subject.trim()) { errors.subject = 'Subject is required'; ok = false }
  return ok
}

async function onSubmit () {
  if (!validate()) return
  saving.value = true
  formError.value = ''
  try {
    await api.post('/interactions', {
      customerId: form.customerId,
      userId: auth.user?.userId || null,
      type: form.type,
      subject: form.subject,
      notes: form.notes
    })
    Object.assign(form, empty())
    await refresh()
  } catch (err) {
    formError.value = err.response?.data?.message || 'Could not save interaction'
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  customers.fetchAll()
  refresh()
})
</script>
