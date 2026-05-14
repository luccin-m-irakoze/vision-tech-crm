<template>
  <header class="sticky top-0 z-20 h-16 bg-white/80 backdrop-blur-md
                 border-b border-slate-200/70">
    <div class="h-full flex items-center justify-between px-4 sm:px-6 lg:px-8">
      <!-- Left: mobile menu + breadcrumb -->
      <div class="flex items-center gap-3 min-w-0">
        <button
          class="btn-icon lg:hidden"
          aria-label="Open menu"
          @click="$emit('toggle-sidebar')">
          <Bars3Icon class="h-6 w-6" />
        </button>
        <div class="hidden sm:block min-w-0">
          <div class="text-[11px] uppercase tracking-widest text-slate-400">
            {{ greeting }}
          </div>
          <h2 class="font-display font-semibold text-vtc-ink truncate">
            {{ auth.fullName }}
          </h2>
        </div>
      </div>

      <!-- Right: search, notifications, user menu -->
      <div class="flex items-center gap-2 sm:gap-3">
        <div class="relative hidden md:block">
          <MagnifyingGlassIcon class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
          <input
            type="text"
            placeholder="Search…"
            class="input pl-9 pr-3 py-2 w-64 text-sm" />
        </div>

        <button class="btn-icon relative" aria-label="Notifications">
          <BellIcon class="h-5 w-5" />
          <span class="absolute top-1.5 right-1.5 h-2 w-2 bg-vtc-danger rounded-full ring-2 ring-white"></span>
        </button>

        <!-- User menu -->
        <div class="relative">
          <button
            class="flex items-center gap-2 pl-2 pr-3 py-1.5 rounded-lg hover:bg-slate-100 transition-colors"
            @click="menuOpen = !menuOpen"
            @blur="closeSoon">
            <div class="h-8 w-8 rounded-lg bg-gradient-to-br from-vtc-accent to-vtc-purple
                        flex items-center justify-center text-white text-xs font-bold shadow-soft">
              {{ initials }}
            </div>
            <div class="hidden sm:block text-left leading-tight">
              <div class="text-xs font-semibold text-vtc-ink">{{ auth.fullName }}</div>
              <div class="text-[10px] text-slate-500">{{ auth.role }}</div>
            </div>
            <ChevronDownIcon class="h-4 w-4 text-slate-400" />
          </button>

          <Transition
            enter-active-class="transition duration-150 ease-out"
            enter-from-class="opacity-0 -translate-y-1"
            enter-to-class="opacity-100 translate-y-0"
            leave-active-class="transition duration-100 ease-in"
            leave-from-class="opacity-100" leave-to-class="opacity-0">
            <div v-if="menuOpen"
                 class="absolute right-0 mt-2 w-56 surface p-2 z-30 animate-pop-in"
                 @mousedown.prevent>
              <div class="px-3 py-2 border-b border-slate-100">
                <div class="text-sm font-semibold text-vtc-ink truncate">{{ auth.fullName }}</div>
                <div class="text-xs text-slate-500 truncate">{{ auth.user?.email || '' }}</div>
              </div>
              <button class="w-full text-left flex items-center gap-2 px-3 py-2 text-sm
                             rounded-md hover:bg-slate-100 text-vtc-ink">
                <UserCircleIcon class="h-4 w-4 text-slate-500" />
                Profile
              </button>
              <button class="w-full text-left flex items-center gap-2 px-3 py-2 text-sm
                             rounded-md hover:bg-slate-100 text-vtc-ink">
                <Cog6ToothIcon class="h-4 w-4 text-slate-500" />
                Settings
              </button>
              <div class="border-t border-slate-100 my-1"></div>
              <button
                class="w-full text-left flex items-center gap-2 px-3 py-2 text-sm
                       rounded-md hover:bg-red-50 text-red-600"
                @click="onLogout">
                <ArrowRightOnRectangleIcon class="h-4 w-4" />
                Sign out
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  Bars3Icon, BellIcon, MagnifyingGlassIcon, ChevronDownIcon,
  UserCircleIcon, Cog6ToothIcon, ArrowRightOnRectangleIcon
} from '@heroicons/vue/24/outline'

defineEmits(['toggle-sidebar'])

const auth = useAuthStore()
const router = useRouter()
const menuOpen = ref(false)

const initials = computed(() => {
  const name = auth.fullName || 'User'
  return name.split(' ').map((p) => p[0]).slice(0, 2).join('').toUpperCase()
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 12) return 'Good morning'
  if (h < 18) return 'Good afternoon'
  return 'Good evening'
})

function closeSoon () {
  setTimeout(() => { menuOpen.value = false }, 150)
}

function onLogout () {
  auth.logout()
  router.push({ name: 'login' })
}
</script>
