<template>
  <AppShell>
    <div class="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-3 mb-6">
      <div>
        <h1 class="page-title">Customers</h1>
        <p class="page-subtitle">Manage company accounts and primary contacts.</p>
      </div>
      <button class="btn-primary" @click="openCreate">
        <PlusIcon class="h-4 w-4" />
        New customer
      </button>
    </div>

    <!-- Search bar -->
    <div class="card mb-5">
      <div class="flex flex-col sm:flex-row gap-3">
        <div class="relative flex-1">
          <MagnifyingGlassIcon class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
          <input
            v-model="searchQuery"
            class="input pl-9"
            placeholder="Search by company name or e-mail…"
            @keyup.enter="onSearch" />
        </div>
        <button class="btn-secondary" @click="onSearch">
          <FunnelIcon class="h-4 w-4" />
          Search
        </button>
      </div>
    </div>

    <LoadingSpinner v-if="store.loading" />
    <ErrorBanner v-else-if="store.error" :message="store.error" />

    <div v-else class="card !p-0 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="table-modern">
          <thead>
            <tr>
              <th>ID</th>
              <th>Company</th>
              <th>Industry</th>
              <th>Contact</th>
              <th>Phone</th>
              <th>E-mail</th>
              <th class="text-right">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in store.items" :key="c.id">
              <td class="text-slate-400 font-medium">#{{ c.id }}</td>
              <td>
                <router-link :to="`/customers/${c.id}`"
                             class="flex items-center gap-2.5 group">
                  <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-vtc-accent to-vtc-purple
                              text-white text-[11px] font-bold flex items-center justify-center
                              shadow-soft">
                    {{ initials(c.companyName) }}
                  </div>
                  <span class="font-semibold text-vtc-ink group-hover:text-vtc-accent2 transition-colors">
                    {{ c.companyName }}
                  </span>
                </router-link>
              </td>
              <td>
                <span v-if="c.industry" class="badge-slate">{{ c.industry }}</span>
                <span v-else class="text-slate-400">—</span>
              </td>
              <td class="text-slate-700">{{ c.contactPerson || '—' }}</td>
              <td class="text-slate-700">{{ c.phone || '—' }}</td>
              <td>
                <a v-if="c.email" :href="`mailto:${c.email}`" class="link">{{ c.email }}</a>
                <span v-else class="text-slate-400">—</span>
              </td>
              <td class="text-right">
                <div class="flex justify-end gap-1">
                  <button class="btn-icon" title="Edit" @click="openEdit(c)">
                    <PencilSquareIcon class="h-4 w-4" />
                  </button>
                  <button class="btn-icon hover:!text-red-600 hover:!bg-red-50"
                          title="Delete" @click="askDelete(c)">
                    <TrashIcon class="h-4 w-4" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!store.items.length">
              <td colspan="7" class="py-12 text-center">
                <div class="flex flex-col items-center gap-2">
                  <UsersIcon class="h-10 w-10 text-slate-300" />
                  <p class="text-sm text-slate-500">No customers yet.</p>
                  <button class="btn-primary btn-sm mt-2" @click="openCreate">
                    <PlusIcon class="h-3.5 w-3.5" />
                    Add your first customer
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create / Edit modal -->
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
              <h3 class="font-display text-lg font-bold text-vtc-ink">
                {{ form.id ? 'Edit customer' : 'New customer' }}
              </h3>
              <button class="btn-icon" @click="showForm = false">
                <XMarkIcon class="h-5 w-5" />
              </button>
            </div>
            <div class="p-6">
              <ErrorBanner :message="formError" class="mb-4" />
              <form class="grid grid-cols-1 sm:grid-cols-2 gap-4" novalidate @submit.prevent="onSave">
                <div class="sm:col-span-2">
                  <label class="label">Company name *</label>
                  <input v-model="form.companyName" class="input" :class="{ 'input-error': errors.companyName }" />
                  <p v-if="errors.companyName" class="text-xs text-red-600 mt-1">{{ errors.companyName }}</p>
                </div>
                <div>
                  <label class="label">Industry</label>
                  <input v-model="form.industry" class="input" />
                </div>
                <div>
                  <label class="label">Contact person</label>
                  <input v-model="form.contactPerson" class="input" />
                </div>
                <div>
                  <label class="label">E-mail</label>
                  <input v-model="form.email" type="email" class="input" :class="{ 'input-error': errors.email }" />
                  <p v-if="errors.email" class="text-xs text-red-600 mt-1">{{ errors.email }}</p>
                </div>
                <div>
                  <label class="label">Phone</label>
                  <input v-model="form.phone" class="input" />
                </div>
                <div class="sm:col-span-2">
                  <label class="label">Address</label>
                  <input v-model="form.address" class="input" />
                </div>
                <div class="sm:col-span-2 flex justify-end gap-2 pt-2">
                  <button type="button" class="btn-secondary" @click="showForm = false">Cancel</button>
                  <button type="submit" class="btn-primary" :disabled="saving">
                    <ArrowPathIcon v-if="saving" class="h-4 w-4 animate-spin" />
                    {{ saving ? 'Saving…' : 'Save' }}
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
      title="Delete customer"
      :message="`Delete ${toDelete?.companyName}? This cannot be undone.`"
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
import { useCustomerStore } from '../stores/customer'
import {
  PlusIcon, MagnifyingGlassIcon, FunnelIcon, PencilSquareIcon,
  TrashIcon, UsersIcon, XMarkIcon, ArrowPathIcon
} from '@heroicons/vue/24/outline'

const store = useCustomerStore()
const searchQuery = ref('')
const showForm = ref(false)
const showConfirm = ref(false)
const saving = ref(false)
const formError = ref('')
const toDelete = ref(null)

const empty = () => ({
  id: null, companyName: '', industry: '', contactPerson: '',
  email: '', phone: '', address: ''
})
const form = reactive(empty())
const errors = reactive({ companyName: '', email: '' })

onMounted(() => store.fetchAll())

function initials (name) {
  return (name || '?').split(' ').map((p) => p[0]).slice(0, 2).join('').toUpperCase()
}

function onSearch () { store.fetchAll(searchQuery.value) }

function openCreate () {
  Object.assign(form, empty())
  formError.value = ''
  showForm.value = true
}
function openEdit (c) {
  Object.assign(form, c)
  formError.value = ''
  showForm.value = true
}
function askDelete (c) { toDelete.value = c; showConfirm.value = true }

function validate () {
  errors.companyName = ''
  errors.email = ''
  let ok = true
  if (!form.companyName.trim()) { errors.companyName = 'Required'; ok = false }
  if (form.email && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) {
    errors.email = 'Invalid e-mail'; ok = false
  }
  return ok
}

async function onSave () {
  if (!validate()) return
  saving.value = true
  formError.value = ''
  try {
    if (form.id) await store.update(form.id, { ...form })
    else await store.create({ ...form })
    showForm.value = false
  } catch (err) {
    formError.value = err.response?.data?.message || 'Save failed'
  } finally {
    saving.value = false
  }
}

async function confirmDelete () {
  if (!toDelete.value) return
  try { await store.remove(toDelete.value.id) } catch (_) {}
  toDelete.value = null
}
</script>
