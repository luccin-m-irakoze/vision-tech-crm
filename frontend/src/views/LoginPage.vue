<template>
  <div class="min-h-screen grid lg:grid-cols-2 bg-gradient-cool text-white">
    <!-- Left: brand panel (hidden on mobile) -->
    <div class="hidden lg:flex relative flex-col justify-between p-12 overflow-hidden">
      <!-- decorative shapes -->
      <div class="absolute -top-24 -left-24 h-96 w-96 rounded-full bg-vtc-accent/30 blur-3xl"></div>
      <div class="absolute bottom-0 right-0 h-[28rem] w-[28rem] rounded-full bg-vtc-purple/30 blur-3xl"></div>

      <div class="relative z-10 flex items-center gap-3">
        <div class="h-12 w-12 rounded-xl bg-gradient-brand flex items-center justify-center shadow-glow">
          <svg viewBox="0 0 24 24" class="h-7 w-7 text-white" fill="currentColor">
            <path d="M5 5h3l4 12 4-12h3l-6 16h-2L5 5z" />
          </svg>
        </div>
        <div>
          <div class="font-display font-bold text-xl">Vision Technologies</div>
          <div class="text-xs uppercase tracking-widest text-slate-300">Customer Relationship Management</div>
        </div>
      </div>

      <div class="relative z-10 max-w-md">
        <h1 class="font-display font-bold text-4xl leading-tight mb-4">
          Customers, leads & support —
          <span class="bg-gradient-to-r from-vtc-accent to-vtc-purple bg-clip-text text-transparent">
            one unified workspace.
          </span>
        </h1>
        <p class="text-slate-300 leading-relaxed">
          Track every conversation, move deals through your pipeline,
          resolve support tickets faster, and report on the metrics that matter.
        </p>

        <ul class="mt-8 space-y-3 text-sm text-slate-200">
          <li class="flex items-center gap-3">
            <span class="h-7 w-7 rounded-lg bg-white/10 flex items-center justify-center">
              <CheckIcon class="h-4 w-4 text-emerald-400" />
            </span>
            Drag-and-drop sales pipeline
          </li>
          <li class="flex items-center gap-3">
            <span class="h-7 w-7 rounded-lg bg-white/10 flex items-center justify-center">
              <CheckIcon class="h-4 w-4 text-emerald-400" />
            </span>
            Priority-aware support ticketing
          </li>
          <li class="flex items-center gap-3">
            <span class="h-7 w-7 rounded-lg bg-white/10 flex items-center justify-center">
              <CheckIcon class="h-4 w-4 text-emerald-400" />
            </span>
            Live KPI dashboards & exports
          </li>
        </ul>
      </div>

      <div class="relative z-10 text-xs text-slate-400">
        &copy; {{ year }} Vision Technologies Ltd. &middot; All rights reserved.
      </div>
    </div>

    <!-- Right: form -->
    <div class="flex items-center justify-center p-6 lg:p-12 bg-gradient-surface text-vtc-ink min-h-screen">
      <div class="w-full max-w-md animate-slide-up">
        <!-- compact brand for mobile -->
        <div class="flex items-center gap-3 mb-8 lg:hidden">
          <div class="h-11 w-11 rounded-xl bg-gradient-brand flex items-center justify-center shadow-glow">
            <svg viewBox="0 0 24 24" class="h-6 w-6 text-white" fill="currentColor">
              <path d="M5 5h3l4 12 4-12h3l-6 16h-2L5 5z" />
            </svg>
          </div>
          <div>
            <div class="font-display font-bold text-vtc-ink">Vision Technologies</div>
            <div class="text-xs text-slate-500">Customer Relationship Management</div>
          </div>
        </div>

        <h2 class="font-display text-2xl font-bold text-vtc-ink">Welcome back</h2>
        <p class="text-sm text-slate-500 mt-1 mb-6">Sign in to continue to your workspace.</p>

        <ErrorBanner :message="serverError" class="mb-4" />

        <form class="space-y-4" novalidate @submit.prevent="onSubmit">
          <div>
            <label class="label" for="email">E-mail</label>
            <div class="relative">
              <EnvelopeIcon class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
              <input
                id="email"
                v-model="form.email"
                type="email"
                autocomplete="username"
                class="input pl-9"
                :class="{ 'input-error': errors.email }"
                placeholder="you@visiontech.rw">
            </div>
            <p v-if="errors.email" class="mt-1 text-xs text-red-600">{{ errors.email }}</p>
          </div>

          <div>
            <div class="flex items-center justify-between">
              <label class="label !mb-0" for="password">Password</label>
              <button type="button" class="text-[11px] text-vtc-accent2 hover:underline">
                Forgot?
              </button>
            </div>
            <div class="relative mt-1.5">
              <LockClosedIcon class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
              <input
                id="password"
                v-model="form.password"
                :type="showPwd ? 'text' : 'password'"
                autocomplete="current-password"
                class="input pl-9 pr-10"
                :class="{ 'input-error': errors.password }"
                placeholder="••••••••">
              <button type="button"
                      class="absolute right-2 top-1/2 -translate-y-1/2 p-1.5 text-slate-400 hover:text-slate-600"
                      @click="showPwd = !showPwd">
                <component :is="showPwd ? EyeSlashIcon : EyeIcon" class="h-4 w-4" />
              </button>
            </div>
            <p v-if="errors.password" class="mt-1 text-xs text-red-600">{{ errors.password }}</p>
          </div>

          <button type="submit" class="btn-primary w-full !py-2.5" :disabled="auth.loading">
            <ArrowPathIcon v-if="auth.loading" class="h-4 w-4 animate-spin" />
            <span>{{ auth.loading ? 'Signing in…' : 'Sign in' }}</span>
            <ArrowRightIcon v-if="!auth.loading" class="h-4 w-4" />
          </button>
        </form>

        <!-- Demo credentials -->
        <div class="mt-6 surface p-4">
          <div class="flex items-center gap-2 mb-2">
            <SparklesIcon class="h-4 w-4 text-vtc-purple" />
            <p class="text-xs font-semibold text-vtc-ink">Demo accounts</p>
          </div>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-1.5 text-[11px]">
            <button v-for="d in demos" :key="d.email"
                    class="text-left rounded-md px-2 py-1.5 hover:bg-slate-100 transition-colors"
                    @click="fillDemo(d)">
              <div class="font-semibold text-vtc-ink truncate">{{ d.role }}</div>
              <div class="text-slate-500 truncate">{{ d.email }}</div>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import ErrorBanner from '../components/ErrorBanner.vue'
import {
  EnvelopeIcon, LockClosedIcon, EyeIcon, EyeSlashIcon, CheckIcon,
  SparklesIcon, ArrowRightIcon, ArrowPathIcon
} from '@heroicons/vue/24/outline'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = reactive({ email: '', password: '' })
const errors = reactive({ email: '', password: '' })
const showPwd = ref(false)
const serverError = computed(() => auth.error || '')
const year = new Date().getFullYear()

const demos = [
  { role: 'Admin',   email: 'admin@visiontech.rw', pwd: 'Admin@123' },
  { role: 'Sales',   email: 'alice@visiontech.rw', pwd: 'Sales@123' },
  { role: 'Support', email: 'eric@visiontech.rw',  pwd: 'Support@123' },
  { role: 'Manager', email: 'diane@visiontech.rw', pwd: 'Manager@123' }
]

function fillDemo (d) {
  form.email = d.email
  form.password = d.pwd
}

function validate () {
  errors.email = ''
  errors.password = ''
  let ok = true
  if (!form.email) { errors.email = 'E-mail is required'; ok = false }
  else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) {
    errors.email = 'Enter a valid e-mail'
    ok = false
  }
  if (!form.password) { errors.password = 'Password is required'; ok = false }
  else if (form.password.length < 4) {
    errors.password = 'Password is too short'
    ok = false
  }
  return ok
}

async function onSubmit () {
  if (!validate()) return
  const ok = await auth.login(form.email, form.password)
  if (ok) {
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
  }
}
</script>
