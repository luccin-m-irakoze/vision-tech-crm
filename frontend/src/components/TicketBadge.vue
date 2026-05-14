<template>
  <span :class="['badge', meta.classes]">
    <span :class="['h-1.5 w-1.5 rounded-full', meta.dot]"></span>
    {{ meta.label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  kind: { type: String, required: true },
  value: { type: String, required: true }
})

const PRIORITY = {
  LOW:      { label: 'Low',      classes: 'bg-slate-50 text-slate-700 ring-1 ring-slate-200',     dot: 'bg-slate-400' },
  MEDIUM:   { label: 'Medium',   classes: 'bg-blue-50 text-blue-700 ring-1 ring-blue-200',         dot: 'bg-blue-500' },
  HIGH:     { label: 'High',     classes: 'bg-amber-50 text-amber-700 ring-1 ring-amber-200',      dot: 'bg-amber-500' },
  CRITICAL: { label: 'Critical', classes: 'bg-red-50 text-red-700 ring-1 ring-red-200',            dot: 'bg-red-500' }
}
const STATUS = {
  OPEN:        { label: 'Open',        classes: 'bg-slate-50 text-slate-700 ring-1 ring-slate-200',  dot: 'bg-slate-400' },
  IN_PROGRESS: { label: 'In progress', classes: 'bg-blue-50 text-blue-700 ring-1 ring-blue-200',     dot: 'bg-blue-500' },
  ESCALATED:   { label: 'Escalated',   classes: 'bg-amber-50 text-amber-700 ring-1 ring-amber-200',  dot: 'bg-amber-500' },
  RESOLVED:    { label: 'Resolved',    classes: 'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200', dot: 'bg-emerald-500' },
  CLOSED:      { label: 'Closed',      classes: 'bg-slate-100 text-slate-700 ring-1 ring-slate-200', dot: 'bg-slate-500' }
}

const meta = computed(() => {
  const map = props.kind === 'priority' ? PRIORITY : STATUS
  return map[props.value] || { label: props.value, classes: 'bg-slate-50 text-slate-700 ring-1 ring-slate-200', dot: 'bg-slate-400' }
})
</script>
