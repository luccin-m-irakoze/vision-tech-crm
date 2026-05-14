<template>
  <AppShell>
    <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 mb-6">
      <div>
        <h1 class="page-title">Sales Pipeline</h1>
        <p class="page-subtitle">Drag deals across stages to update them in real time.</p>
      </div>
      <button class="btn-primary" @click="openCreate">
        <PlusIcon class="h-4 w-4" />
        New lead
      </button>
    </div>

    <LoadingSpinner v-if="leads.loading" />
    <ErrorBanner v-else-if="leads.error" :message="leads.error" />

    <div v-else class="grid grid-cols-1 md:grid-cols-3 xl:grid-cols-5 gap-4">
      <div v-for="(stage, idx) in stages" :key="stage"
           class="flex flex-col rounded-2xl bg-white/70 border border-slate-200/70
                  shadow-soft min-h-[420px] transition-colors hover:border-vtc-accent/30"
           @dragover.prevent
           @drop="onDrop($event, stage)">
        <div class="flex items-center justify-between px-4 py-3 border-b border-slate-200/70">
          <div class="flex items-center gap-2">
            <span class="h-2 w-2 rounded-full" :class="stageDot(idx)"></span>
            <h3 class="font-semibold text-sm text-vtc-ink">{{ niceLabel(stage) }}</h3>
          </div>
          <span class="badge-slate">{{ leads.byStage(stage).length }}</span>
        </div>
        <div class="flex-1 p-3 space-y-2 overflow-y-auto">
          <LeadCard v-for="lead in leads.byStage(stage)" :key="lead.id" :lead="lead" />
          <div v-if="!leads.byStage(stage).length"
               class="h-24 flex items-center justify-center text-xs text-slate-400
                      border-2 border-dashed border-slate-200 rounded-xl">
            Drop leads here
          </div>
        </div>
      </div>
    </div>

    <!-- Create lead modal -->
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
              <h3 class="font-display text-lg font-bold text-vtc-ink">New lead</h3>
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
                  <label class="label">Title *</label>
                  <input v-model="form.title" class="input" :class="{ 'input-error': errors.title }" />
                  <p v-if="errors.title" class="text-xs text-red-600 mt-1">{{ errors.title }}</p>
                </div>
                <div class="grid grid-cols-2 gap-3">
                  <div>
                    <label class="label">Estimated value (RWF)</label>
                    <input v-model.number="form.estimatedValue" type="number" min="0" class="input" />
                  </div>
                  <div>
                    <label class="label">Expected close</label>
                    <input v-model="form.expectedClose" type="date" class="input" />
                  </div>
                </div>
                <div>
                  <label class="label">Stage</label>
                  <select v-model="form.stage" class="input">
                    <option v-for="s in stages" :key="s" :value="s">{{ niceLabel(s) }}</option>
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
  </AppShell>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import AppShell from '../components/AppShell.vue'
import LoadingSpinner from '../components/LoadingSpinner.vue'
import ErrorBanner from '../components/ErrorBanner.vue'
import LeadCard from '../components/LeadCard.vue'
import { useLeadStore, LEAD_STAGES } from '../stores/lead'
import { useCustomerStore } from '../stores/customer'
import { PlusIcon, XMarkIcon, ArrowPathIcon } from '@heroicons/vue/24/outline'

const leads = useLeadStore()
const customers = useCustomerStore()
const stages = LEAD_STAGES

const showForm = ref(false)
const saving = ref(false)
const formError = ref('')

const empty = () => ({
  customerId: '', title: '', estimatedValue: 0, expectedClose: '', stage: 'NEW'
})
const form = reactive(empty())
const errors = reactive({ customerId: '', title: '' })

onMounted(() => {
  leads.fetchAll()
  customers.fetchAll()
})

function niceLabel (s) {
  return s.replace('_', ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase())
}

const stageColors = ['bg-slate-400', 'bg-blue-500', 'bg-purple-500', 'bg-emerald-500', 'bg-amber-500', 'bg-rose-500']
function stageDot (idx) { return stageColors[idx % stageColors.length] }

function openCreate () {
  Object.assign(form, empty())
  formError.value = ''
  showForm.value = true
}

function validate () {
  errors.customerId = ''
  errors.title = ''
  let ok = true
  if (!form.customerId) { errors.customerId = 'Select a customer'; ok = false }
  if (!form.title.trim()) { errors.title = 'Title is required'; ok = false }
  return ok
}

async function onSave () {
  if (!validate()) return
  saving.value = true
  formError.value = ''
  try {
    await leads.create({
      customerId: form.customerId,
      title: form.title,
      estimatedValue: form.estimatedValue || 0,
      expectedClose: form.expectedClose || null,
      stage: form.stage
    })
    showForm.value = false
  } catch (err) {
    formError.value = err.response?.data?.message || 'Could not create lead'
  } finally {
    saving.value = false
  }
}

async function onDrop (event, stage) {
  const id = Number(event.dataTransfer.getData('application/vtc-lead-id'))
  if (!id) return
  try { await leads.moveStage(id, stage) } catch (_) {}
}
</script>
