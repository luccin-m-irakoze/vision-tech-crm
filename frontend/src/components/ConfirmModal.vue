<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0" enter-to-class="opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100" leave-to-class="opacity-0">
      <div v-if="modelValue"
           class="fixed inset-0 z-50 flex items-center justify-center
                  bg-vtc-ink/40 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl shadow-lift w-full max-w-md
                    border border-slate-100 animate-pop-in overflow-hidden">
          <div class="p-6">
            <div class="flex items-start gap-4">
              <div class="h-10 w-10 rounded-full bg-red-100 text-red-600
                          flex items-center justify-center shrink-0">
                <ExclamationTriangleIcon class="h-5 w-5" />
              </div>
              <div class="min-w-0">
                <h3 class="font-display text-lg font-bold text-vtc-ink">{{ title }}</h3>
                <p class="text-sm text-slate-600 mt-1">{{ message }}</p>
              </div>
            </div>
          </div>
          <div class="bg-slate-50 px-6 py-3 flex justify-end gap-2 border-t border-slate-100">
            <button class="btn-secondary btn-sm" @click="$emit('update:modelValue', false)">
              Cancel
            </button>
            <button class="btn-danger btn-sm" @click="onConfirm">
              {{ confirmLabel }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ExclamationTriangleIcon } from '@heroicons/vue/24/outline'

defineProps({
  modelValue: { type: Boolean, required: true },
  title:        { type: String, default: 'Confirm action' },
  message:      { type: String, default: 'Are you sure?' },
  confirmLabel: { type: String, default: 'Confirm' }
})
const emit = defineEmits(['update:modelValue', 'confirm'])

function onConfirm () {
  emit('confirm')
  emit('update:modelValue', false)
}
</script>
