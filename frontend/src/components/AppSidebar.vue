<template>
  <aside
    class="fixed inset-y-0 left-0 z-40 w-64 bg-gradient-sidebar text-slate-200
           border-r border-white/5 flex flex-col
           transform transition-transform duration-300 lg:translate-x-0 lg:static lg:z-0"
    :class="open ? 'translate-x-0' : '-translate-x-full'">

    <!-- Brand -->
    <div class="h-16 flex items-center gap-3 px-5 border-b border-white/10">
      <div class="h-10 w-10 rounded-xl bg-gradient-brand flex items-center justify-center shadow-glow">
        <svg viewBox="0 0 24 24" class="h-6 w-6 text-white" fill="currentColor">
          <path d="M5 5h3l4 12 4-12h3l-6 16h-2L5 5z" />
        </svg>
      </div>
      <div class="leading-tight">
        <div class="font-display font-bold text-white tracking-tight">Vision Tech</div>
        <div class="text-[11px] uppercase tracking-widest text-slate-400">CRM Suite</div>
      </div>
    </div>

    <!-- Navigation -->
    <nav class="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
      <p class="px-3 pt-2 pb-1 text-[10px] font-semibold uppercase tracking-widest text-slate-500">
        Main
      </p>
      <router-link
        v-for="item in items"
        :key="item.to"
        :to="item.to"
        class="group flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium
               text-slate-300 hover:text-white hover:bg-white/5
               transition-all duration-150"
        active-class="!bg-gradient-brand !text-white shadow-glow"
        @click="$emit('close')">
        <component :is="item.icon" class="h-5 w-5 shrink-0 opacity-90 group-hover:opacity-100" />
        <span class="truncate">{{ item.label }}</span>
        <span v-if="item.badge"
              class="ml-auto text-[10px] font-semibold px-1.5 py-0.5 rounded-full
                     bg-white/15 text-white">
          {{ item.badge }}
        </span>
      </router-link>
    </nav>

    <!-- Footer card -->
    <div class="p-3">
      <div class="rounded-xl p-4 bg-white/5 border border-white/10">
        <div class="flex items-center gap-3">
          <div class="h-9 w-9 rounded-lg bg-gradient-to-br from-vtc-purple to-vtc-accent2
                      flex items-center justify-center text-white text-sm font-bold">
            {{ initials }}
          </div>
          <div class="min-w-0">
            <div class="text-sm font-semibold text-white truncate">{{ auth.fullName }}</div>
            <div class="text-[11px] text-slate-400 truncate">{{ auth.role || 'Member' }}</div>
          </div>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import {
  HomeIcon, UsersIcon, RectangleStackIcon, ChatBubbleLeftRightIcon,
  LifebuoyIcon, ChartBarIcon
} from '@heroicons/vue/24/outline'

defineProps({ open: { type: Boolean, default: false } })
defineEmits(['close'])

const auth = useAuthStore()

const initials = computed(() => {
  const name = auth.fullName || 'User'
  return name.split(' ').map((p) => p[0]).slice(0, 2).join('').toUpperCase()
})

const items = [
  { to: '/dashboard',    label: 'Dashboard',       icon: HomeIcon },
  { to: '/customers',    label: 'Customers',       icon: UsersIcon },
  { to: '/leads',        label: 'Sales Pipeline',  icon: RectangleStackIcon },
  { to: '/interactions', label: 'Interactions',    icon: ChatBubbleLeftRightIcon },
  { to: '/tickets',      label: 'Support Tickets', icon: LifebuoyIcon },
  { to: '/reports',      label: 'Reports',         icon: ChartBarIcon }
]
</script>
