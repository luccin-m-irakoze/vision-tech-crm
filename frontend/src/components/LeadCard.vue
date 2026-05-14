<template>
  <div
    class="group bg-white border border-slate-200 rounded-xl p-3 shadow-soft
           cursor-grab active:cursor-grabbing hover:shadow-card hover:border-vtc-accent/40
           transition-all duration-200"
    draggable="true"
    @dragstart="onDragStart">
    <div class="flex items-center justify-between text-[11px] text-slate-500 mb-1">
      <span class="font-semibold">#{{ lead.id }}</span>
      <Bars3BottomRightIcon class="h-3.5 w-3.5 text-slate-300 group-hover:text-slate-400" />
    </div>
    <div class="font-semibold text-sm text-vtc-ink leading-snug mb-2">{{ lead.title }}</div>
    <div class="flex items-center gap-2 text-[11px] text-slate-500 mb-2">
      <BuildingOffice2Icon class="h-3.5 w-3.5" />
      <span class="truncate">{{ lead.customerName }}</span>
    </div>
    <div class="flex items-center justify-between text-[11px]">
      <span class="badge-blue">
        <BanknotesIcon class="h-3 w-3" />
        RWF {{ formatValue(lead.estimatedValue) }}
      </span>
      <span v-if="lead.expectedClose" class="text-slate-400 flex items-center gap-1">
        <CalendarDaysIcon class="h-3 w-3" />
        {{ lead.expectedClose }}
      </span>
    </div>
  </div>
</template>

<script setup>
import {
  BuildingOffice2Icon, BanknotesIcon, CalendarDaysIcon, Bars3BottomRightIcon
} from '@heroicons/vue/24/outline'

const props = defineProps({
  lead: { type: Object, required: true }
})

function onDragStart (event) {
  event.dataTransfer.setData('application/vtc-lead-id', String(props.lead.id))
  event.dataTransfer.effectAllowed = 'move'
}

function formatValue (v) {
  if (v == null) return '0'
  return Number(v).toLocaleString()
}
</script>
