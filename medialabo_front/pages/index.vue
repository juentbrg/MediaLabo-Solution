<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold mb-4">Patient List</h1>
    <ul class="space-y-2">
      <li
          v-for="patient in patients"
          :key="patient.id"
          class="p-4 border rounded-lg hover:bg-gray-50 transition"
      >
        <NuxtLink :to="`/patient/${patient.id}`" class="text-blue-600 hover:underline">
          {{ patient.firstName }} {{ patient.lastName }}
        </NuxtLink>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Patient {
  id: string
  firstName: string
  lastName: string
}

const patients = ref<Patient[]>([])

onMounted(async () => {
  const res = await fetch('http://localhost:8080/api/patient')
  if (res.ok) {
    patients.value = await res.json()
  }
})
</script>
