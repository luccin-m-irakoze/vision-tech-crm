<template>
  <div class="min-h-screen flex bg-gradient-surface">
    <AppSidebar
      :open="sidebarOpen"
      @close="sidebarOpen = false" />

    <div class="flex-1 flex flex-col min-w-0">
      <AppNavbar @toggle-sidebar="sidebarOpen = !sidebarOpen" />

      <main class="flex-1 overflow-y-auto">
        <div class="max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-6 lg:py-8 animate-fade-in">
          <slot />
        </div>
      </main>
    </div>

    <!-- Mobile sidebar overlay -->
    <Transition
      enter-active-class="transition-opacity duration-200"
      enter-from-class="opacity-0" enter-to-class="opacity-100"
      leave-active-class="transition-opacity duration-200"
      leave-from-class="opacity-100" leave-to-class="opacity-0">
      <div v-if="sidebarOpen"
           class="fixed inset-0 z-30 bg-vtc-ink/40 backdrop-blur-sm lg:hidden"
           @click="sidebarOpen = false" />
    </Transition>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppNavbar from './AppNavbar.vue'
import AppSidebar from './AppSidebar.vue'

const sidebarOpen = ref(false)
</script>
