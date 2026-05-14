<template>
  <div class="card card-hover">
    <div class="flex items-start justify-between gap-4">
      <div class="flex items-center gap-4 min-w-0">
        <div class="h-14 w-14 rounded-2xl bg-gradient-to-br from-vtc-accent to-vtc-purple
                    text-white font-display font-bold text-lg flex items-center justify-center
                    shadow-soft shrink-0">
          {{ initials }}
        </div>
        <div class="min-w-0">
          <h3 class="font-display text-xl font-bold text-vtc-ink truncate">
            {{ customer.companyName }}
          </h3>
          <p class="text-xs text-slate-500">
            <span v-if="customer.industry" class="badge-slate">{{ customer.industry }}</span>
            <span v-else>No industry set</span>
          </p>
        </div>
      </div>
      <span class="badge-blue shrink-0">#{{ customer.id }}</span>
    </div>

    <dl class="mt-5 grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
      <div class="rounded-lg bg-slate-50 p-3">
        <dt class="text-[11px] font-semibold uppercase tracking-widest text-slate-500 flex items-center gap-1.5">
          <UserIcon class="h-3 w-3" /> Contact
        </dt>
        <dd class="text-vtc-ink font-medium mt-1">{{ customer.contactPerson || '—' }}</dd>
      </div>
      <div class="rounded-lg bg-slate-50 p-3">
        <dt class="text-[11px] font-semibold uppercase tracking-widest text-slate-500 flex items-center gap-1.5">
          <PhoneIcon class="h-3 w-3" /> Phone
        </dt>
        <dd class="text-vtc-ink font-medium mt-1">
          <a v-if="customer.phone" :href="`tel:${customer.phone}`" class="link">{{ customer.phone }}</a>
          <span v-else>—</span>
        </dd>
      </div>
      <div class="rounded-lg bg-slate-50 p-3">
        <dt class="text-[11px] font-semibold uppercase tracking-widest text-slate-500 flex items-center gap-1.5">
          <EnvelopeIcon class="h-3 w-3" /> E-mail
        </dt>
        <dd class="text-vtc-ink font-medium mt-1 truncate">
          <a v-if="customer.email" :href="`mailto:${customer.email}`" class="link">{{ customer.email }}</a>
          <span v-else>—</span>
        </dd>
      </div>
      <div v-if="customer.address" class="sm:col-span-3 rounded-lg bg-slate-50 p-3">
        <dt class="text-[11px] font-semibold uppercase tracking-widest text-slate-500 flex items-center gap-1.5">
          <MapPinIcon class="h-3 w-3" /> Address
        </dt>
        <dd class="text-vtc-ink font-medium mt-1">{{ customer.address }}</dd>
      </div>
    </dl>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { UserIcon, PhoneIcon, EnvelopeIcon, MapPinIcon } from '@heroicons/vue/24/outline'

const props = defineProps({
  customer: { type: Object, required: true }
})

const initials = computed(() => {
  const name = props.customer.companyName || '?'
  return name.split(' ').map((p) => p[0]).slice(0, 2).join('').toUpperCase()
})
</script>
