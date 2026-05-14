<template>
  <AppShell>
    <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 mb-6">
      <div>
        <h1 class="page-title">Support tickets</h1>
        <p class="page-subtitle">Triage, escalate and resolve customer issues.</p>
      </div>
      <button class="btn-primary" @click="openCreate">
        <PlusIcon class="h-4 w-4" />
        New ticket
      </button>
    </div>

    <LoadingSpinner v-if="loading" />
    <ErrorBanner v-else-if="error" :message="error" />

    <div v-else class="card !p-0 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="table-modern">
          <thead>
            <tr>
              <th>ID</th>
              <th>Subject</th>
              <th>Customer</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Created</th>
              <th class="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="t in tickets" :key="t.id">
              <td class="text-slate-400 font-medium">#{{ t.id }}</td>
              <td class="font-semibold text-vtc-ink">{{ t.subject }}</td>
              <td class="text-slate-700">{{ t.customerName }}</td>
              <td><TicketBadge kind="priority" :value="t.priority" /></td>
              <td><TicketBadge kind="status" :value="t.status" /></td>
              <td class="text-xs text-slate-500">{{ format(t.createdAt) }}</td>
              <td class="text-right">
                <div class="flex justify-end gap-1">
                  <button
                    v-if="t.status !== 'ESCALATED' && t.status !== 'RESOLVED'"
                    class="btn-icon hover:!text-amber-600 hover:!bg-amber-50"
                    title="Escalate"
                    @click="escalate(t.id)">
                    <ArrowUpCircleIcon class="h-4 w-4" />
                  </button>
                  <button
                    v-if="t.status !== 'RESOLVED'"
                    class="btn-icon hover:!text-emerald-600 hover:!bg-emerald-50"
                    title="Resolve"
                    @click="resolve(t.id)">
                    <CheckCircleIcon class="h-4 w-4" />
                  </button>
                  <button
                    class="btn-icon hover:!text-red-600 hover:!bg-red-50"
                    title="Delete"
                    @click="askDelete(t)">
                    <TrashIcon class="h-4 w-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!tickets.length">
              <td colspan="7" class="py-12 text-center">
                <div class="flex flex-col items-center gap-2">
                  <LifebuoyIcon class="h-10 w-10 text-slate-300" />
                  <p class="text-sm text-slate-500">No tickets yet.</p>
                  <button class="btn-primary btn-sm mt-2" @click="openCreate">
                    <PlusIcon class="h-3.5 w-3.5" />
                    Create your first ticket
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create modal -->
    <Teleport to="body">
      <Transition
        enter-active-class="transition duration-200 ease-out"
        enter-from-class="opacity-0" enter-to-class="opacity-100"
        leave-active-class="transition duration-150 ease-in"
        leave-from-class="opacity-100" leave-to-class="opacity-0">
        <div v-if="showForm"
             class="fixed inset-0 z-50 flex items-center justify-center
                    bg-vtc-ink/40 backdrop-blur-sm p-4">
          <div class="bg-white rounded-2xl shadow-lift w-full max-w-lg
                      border border-slate-100 animate-pop-in overflow-hidden">
            <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
              <h3 class="font-display text-lg font-bold text-vtc-ink">New support ticket</h3>
              <button class="btn-icon" @click="showForm = false">
                <XMarkIcon class="h-5 w-5" />
              </button>
            </div>
            <div class="p-6">
              <ErrorBanner :message="formError" class="mb-4" />
              <form class="space-y-4" novalidate @submit.prevent="onSave">
                <div>
                  <label class="label">Customer *</label>
                  <select v-model="form.customerId" class="input" :class="{ 'input-error': errors.customerId }">
                    <option value="">— select —</option>
                    <option v-for="c in customers.items" :key="c.id" :value="c.id">{{ c.companyName }}</option>
                  </select>
                  <p v-if="errors.customerId" class="text-xs text-red-600 mt-1">{{ errors.customerId }}</p>
                </div>
                <div>
                  <label class="label">Subject *</label>
                  <input v-model="form.subject" class="input" :class="{ 'input-error': errors.subject }" />
                  <p v-if="errors.subject" class="text-xs text-red-600 mt-1">{{ errors.subject }}</p>
                </div>
                <div>
                  <label class="label">Description</label>
                  <textarea v-model="form.description" rows="4" class="input"></textarea>
                </div>
                <div>
                  <label class="label">Priority</label>
                  <select v-model="form.priority" class="input">
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="CRITICAL">Critical</option>
                  </select>
                </div>
                <div class="flex justify-end gap-2 pt-2">
                  <button type="button" class="btn-secondary" @click="showForm = false">Cancel</button>
                  <button type="submit" class="btn-primary" :disabled="saving">
                    <ArrowPathIcon v-if="saving" class="h-4 w-4 animate-spin" />
                    {{ saving ? 'Saving…' : 'Create' }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <ConfirmModal
      v-model="showConfirm"
      title="Delete ticket"
      :message="`Delete ticket #${toDelete?.id}? This cannot be undone.`"
      confirm-label="Delete"
      @confirm="confirmDelete" />
  </AppShell>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppShell from '../components/AppShell.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorBanner from '../components/ErrorBanner.vue'
import ConfirmModal from '../components/ConfirmModal.vue'
import TicketBadge from '../components/TicketBadge.vue'
import { useCustomerStore } from '../stores/customer'
import api from '../services/api'
import {
  PlusIcon, ArrowUpCircleIcon, CheckCircleIcon, TrashIcon,
  XMarkIcon, ArrowPathIcon, LifebuoyIcon
} from '@heroicons/vue/24/outline'

const customers = useCustomerStore()
const tickets = ref([])
const loading = ref(true)
const error = ref('')
const showForm = ref(false)
const showConfirm = ref(false)
const saving = ref(false)
const formError = ref('')
const toDelete = ref(null)

const empty = () => ({ customerId: '', subject: '', description: '', priority: 'MEDIUM' })
const form = reactive(empty())
const errors = reactive({ customerId: '', subject: '' })

function format (d) { try { return new Date(d).toLocaleString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) } catch { return d } }

async function refresh () {
  loading.value = true; error.value = ''
  try {
    const { data } = await api.get('/tickets')
    tickets.value = data
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to load tickets'
  } finally {
    loading.value = false
  }
}

function openCreate () {
  Object.assign(form, empty())
  formError.value = ''
  showForm.value = true
}

function validate () {
  errors.customerId = ''
  errors.subject = ''
  let ok = true
  if (!form.customerId) { errors.customerId = 'Select a customer'; ok = false }
  if (!form.subject.trim()) { errors.subject = 'Subject is required'; ok = false }
  return ok
}

async function onSave () {
  if (!validate()) return
  saving.value = true; formError.value = ''
  try {
    await api.post('/tickets', { ...form })
    showForm.value = false
    await refresh()
  } catch (err) {
    formError.value = err.response?.data?.message || 'Could not save ticket'
  } finally {
    saving.value = false
  }
}

async function escalate (id) {
  try { await api.put(`/tickets/${id}/escalate`); await refresh() } catch (_) {}
}
async function resolve (id) {
  try { await api.put(`/tickets/${id}/resolve`); await refresh() } catch (_) {}
}
function askDelete (t) { toDelete.value = t; showConfirm.value = true }
async function confirmDelete () {
  if (!toDelete.value) return
  try { await api.delete(`/tickets/${toDelete.value.id}`); await refresh() } catch (_) {}
  toDelete.value = null
}

onMounted(() => {
  customers.fetchAll()
  refresh()
})
</script>
