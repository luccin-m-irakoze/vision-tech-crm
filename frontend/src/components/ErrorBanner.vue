<template>
  <div v-if="message"
       class="flex items-start gap-3 rounded-xl border border-red-200 bg-red-50/80 p-3.5 text-sm text-red-800
              animate-fade-in">
    <ExclamationCircleIcon class="h-5 w-5 text-red-500 shrink-0 mt-0.5" />
    <div class="min-w-0">
      <strong class="font-semibold">Error</strong>
      <span class="ml-1">— {{ message }}</span>
    </div>
  </div>
</template>

<script setup>
import { ExclamationCircleIcon } from '@heroicons/vue/24/outline'

defineProps({ message: { type: String, default: '' } })
</script>
