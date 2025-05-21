'use client'

import {useEffect, useState, ChangeEvent, JSX} from 'react'
import axios from 'axios'
import AuthGuard from '@/components/AuthGuard'
import AddPatientModal from '@/components/AddPatientModal'
import EditPatientModal from '@/components/EditPatientModal'
import PatientTable from '@/components/PatientTable'
import {useRouter} from "next/navigation";

export interface Patient {
  id?: string
  firstName: string
  lastName: string
  birthDate: string
  address?: string
  phone?: string
  gender: 'MALE' | 'FEMALE'
  risque?: string
}

export default function HomePage(): JSX.Element {
  const router = useRouter()
  const [patients, setPatients] = useState<Patient[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [shouldRefresh, setShouldRefresh] = useState<boolean>(false)

  const [isAddOpen, setIsAddOpen] = useState<boolean>(false)
  const [addForm, setAddForm] = useState<Patient>({
    firstName: '',
    lastName: '',
    birthDate: '',
    address: '',
    phone: '',
    gender: 'MALE',
  })

  const [isEditOpen, setIsEditOpen] = useState<boolean>(false)
  const [editForm, setEditForm] = useState<Patient>({
    id: undefined,
    firstName: '',
    lastName: '',
    birthDate: '',
    address: '',
    phone: '',
    gender: 'MALE',
  })

  const fetchPatients = async (): Promise<void> => {
    setLoading(true)
    try {
      const res = await axios.get<Patient[]>('http://localhost:8080/api/patient', {
        withCredentials: true,
      })
      setPatients(Array.isArray(res.data) ? res.data : [])
    } catch (err) {
      console.error('Error on fetching patients', err)
      setPatients([])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPatients()
  }, [])

  useEffect(() => {
    if (shouldRefresh) {
      fetchPatients()
      setShouldRefresh(false)
    }
  }, [shouldRefresh])

  const handleAddSubmit = async (patient: Patient): Promise<void> => {
    try {
      await axios.post('http://localhost:8080/api/patient/insert', patient, {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
      })
      setIsAddOpen(false)
      setAddForm({
        firstName: '',
        lastName: '',
        birthDate: '',
        address: '',
        phone: '',
        gender: 'MALE',
      })
      setShouldRefresh(true)
    } catch (err) {
      console.error('Erreur ajout patient', err)
    }
  }

  const handleViewPatient = (id: string): void => {
    router.push(`/patient/${id}`)
  }

  const handleAddChange = (
      e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ): void => {
    setAddForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }
  const handleEditChange = (
      e: ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ): void => {
    setEditForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
  }

  const handleDeletePatient = async (id: string): Promise<void> => {
    try {
      await axios.delete(`http://localhost:8080/api/patient/delete/${id}`, {
        withCredentials: true,
      })
      setShouldRefresh(true)
    } catch (err) {
      console.error('Erreur suppression patient', err)
    }
  }

  const handleUpdatePatient = (patient: Patient): void => {
    setEditForm(patient)
    setIsEditOpen(true)
  }

  const handleEditSubmit = async (patient: Patient): Promise<void> => {
    if (!patient.id) return
    try {
      await axios.put(`http://localhost:8080/api/patient/${patient.id}`, patient, {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
      })
      setIsEditOpen(false)
      setEditForm({
        id: undefined,
        firstName: '',
        lastName: '',
        birthDate: '',
        address: '',
        phone: '',
        gender: 'MALE',
      })
      setShouldRefresh(true)
    } catch (err) {
      console.error('Erreur mise à jour patient', err)
    }
  }

  return (
      <AuthGuard>
        <PatientTable
            patients={patients}
            loading={loading}
            onView={handleViewPatient}
            onDelete={handleDeletePatient}
            onUpdate={handleUpdatePatient}
            onAddClick={() => setIsAddOpen(true)}
        />

        <AddPatientModal
            isOpen={isAddOpen}
            onClose={() => setIsAddOpen(false)}
            onSubmit={handleAddSubmit}
            formData={addForm}
            onChange={handleAddChange}
        />

        <EditPatientModal
            isOpen={isEditOpen}
            onClose={() => setIsEditOpen(false)}
            onSubmit={handleEditSubmit}
            formData={editForm}
            onChange={handleEditChange}
        />
      </AuthGuard>
  )
}
