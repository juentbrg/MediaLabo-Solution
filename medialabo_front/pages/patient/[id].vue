<template>
  <div class="p-8">
    <div v-if="patient" class="max-w-md mx-auto bg-white shadow-md rounded-lg p-6">
      <h2 class="text-xl font-semibold mb-2">
        {{ patient.firstName }} {{ patient.lastName }}
      </h2>
      <p><strong>Birthdate:</strong> {{ formatDate(patient.birthDate) }}</p>
      <p><strong>Gender:</strong> {{ patient.gender }}</p>
      <p><strong>Address:</strong> {{ patient.address }}</p>
      <p><strong>Phone:</strong> {{ patient.phone }}</p>
    </div>
    <div v-else class="text-center text-gray-500">
      Loading...
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'

interface Patient {
  _id: string
  firstName: string
  lastName: string
  birthDate: string
  gender: string
  address: string
  phone: string
}

const route = useRoute()
const patient = ref<Patient | null>(null)

onMounted(async () => {
  const res = await fetch(`http://localhost:8080/api/patient/${route.params.id}`)
  if (res.ok) {
    patient.value = await res.json()
  }
})

const formatDate = (dateStr: string): string => {
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}
</script>
