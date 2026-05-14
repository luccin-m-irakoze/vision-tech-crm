<template>
  <div class="flex items-center justify-center py-10" role="status" aria-live="polite">
    <div class="relative h-10 w-10">
      <div class="absolute inset-0 rounded-full border-2 border-slate-200"></div>
      <div class="absolute inset-0 rounded-full border-2 border-transparent
                  border-t-vtc-accent border-r-vtc-purple animate-spin"></div>
    </div>
    <span class="ml-3 text-sm text-slate-500">{{ label }}</span>
  </div>
</template>

<script setup>
defineProps({ label: { type: String, default: 'Loading…' } })
</script>
